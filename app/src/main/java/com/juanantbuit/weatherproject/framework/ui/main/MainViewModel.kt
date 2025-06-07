package com.juanantbuit.weatherproject.framework.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.juanantbuit.weatherproject.domain.models.CityInfoModel
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel
import com.juanantbuit.weatherproject.domain.models.NextDayInfoModel
import com.juanantbuit.weatherproject.framework.helpers.DataStoreHelper
import com.juanantbuit.weatherproject.usecases.GetCityInfoUseCase
import com.juanantbuit.weatherproject.usecases.GetForecastResponseUseCase
import com.juanantbuit.weatherproject.usecases.GetNextDaysInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCityInfoUseCase: GetCityInfoUseCase,
    private val getForecastResponseUseCase: GetForecastResponseUseCase,
    private val getNextDaysInfoUseCase: GetNextDaysInfoUseCase,
    private val dataStoreHelper: DataStoreHelper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _currentDay = MutableLiveData<Int?>()
    val currentDay: LiveData<Int?> get() = _currentDay

    private val _cityInfo = MutableLiveData<CityInfoModel?>()
    val cityInfo: LiveData<CityInfoModel?> get() = _cityInfo

    private val _forecastResponse = MutableLiveData<ForecastResponseModel?>()
    val forecastResponse: LiveData<ForecastResponseModel?> get() = _forecastResponse

    private val _coordinates = MutableLiveData<HashMap<String, Float?>?>()
    val coordinates: LiveData<HashMap<String, Float?>?> get() = _coordinates

    private val _geonameId = MutableLiveData<String>()
    val geonameId: LiveData<String> get() = _geonameId

    fun getCurrentDay() {
        val calendar = Calendar.getInstance()
        //Subtraction needed for the number (1..7) to match the indexes of DAYS_OF_WEEK (0..6)
        val result = calendar[Calendar.DAY_OF_WEEK] - 1
        _currentDay.postValue(result)
    }

    fun getCityInfoByGeonameId(geoId: String) {
        viewModelScope.launch {
            val result = getCityInfoUseCase.getCityInfo(geoId)
            _cityInfo.postValue(result)
        }
    }

    fun getForecastResponseByGeonameId(geoId: String) {
        viewModelScope.launch {
            val result = getForecastResponseUseCase.getForecastResponse(geoId)
            _forecastResponse.postValue(result)
        }
    }

    fun getCityInfoByCoordinates(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            val result = getCityInfoUseCase.getCityInfo(latitude, longitude)
            _cityInfo.postValue(result)
        }
    }

    fun getForecastResponseByCoordinates(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            val result = getForecastResponseUseCase.getForecastResponse(latitude, longitude)
            _forecastResponse.postValue(result)
        }
    }

    @SuppressLint("MissingPermission")
    fun getCoordinatesFromGPS(activity: MainActivity, locationRequest: LocationRequest) {
        val coordinates: HashMap<String, Float?> = hashMapOf()

        LocationServices.getFusedLocationProviderClient(activity)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    LocationServices.getFusedLocationProviderClient(activity)
                        .removeLocationUpdates(this)

                    if (locationResult.locations.isNotEmpty()) {

                        val index: Int = locationResult.locations.size - 1
                        val latitude = locationResult.locations[index].latitude.toFloat()
                        val longitude = locationResult.locations[index].longitude.toFloat()

                        coordinates["latitude"] = latitude
                        coordinates["longitude"] = longitude

                        saveIsFirstAppStar(false)
                        _coordinates.postValue(coordinates)
                    }
                }
        }, Looper.getMainLooper())
    }

    fun setLastSavedCoordinates() {
        viewModelScope.launch(dispatcher) {
            val coordinates: HashMap<String, Float?> = hashMapOf()

            dataStoreHelper.read(DataStoreHelper.LAST_LATITUDE_KEY).collect { lastLatitude ->
                coordinates["latitude"] = lastLatitude as Float? ?: 0f

                dataStoreHelper.read(DataStoreHelper.LAST_LONGITUDE_KEY).collect { lastLongitude ->
                    coordinates["longitude"] = lastLongitude as Float? ?: 0f
                    _coordinates.postValue(coordinates)
                }
            }
        }
    }

    fun setLastSavedGeonameId() {
        if(!lastSavedIsCoordinated()) {
            viewModelScope.launch(dispatcher) {
                dataStoreHelper.read(DataStoreHelper.LAST_GEO_ID_KEY).collect { savedGeonameId ->
                    val geoId = savedGeonameId as String? ?: "3117735"
                    _geonameId.postValue(geoId)
                }
            }
        }
    }

    fun setGeonameId(geoIdkey: String) {
        var notExecuted = true

        if(!lastSavedIsCoordinated()) {
            viewModelScope.launch(dispatcher) {
                dataStoreHelper.readGeonameId(geoIdkey).collect { savedGeonameId ->
                    val geoId = savedGeonameId ?: "3117735"
                    if(notExecuted) {
                        saveLastGeonameId(geoId)
                        notExecuted = false
                    }
                }
            }
        }
    }

    fun lastSavedIsCoordinated(): Boolean {
        var result = false
        viewModelScope.launch {
            dataStoreHelper.read(DataStoreHelper.LAST_SAVED_ARE_COORDINATES)
                .collect { lastSavedIsCoordinated ->
                    result = lastSavedIsCoordinated as Boolean? ?: false
                }
        }
        return result
    }

    fun isFirstAppStart(): Boolean {
        var result = true

        viewModelScope.launch {
            dataStoreHelper.read(DataStoreHelper.FIRST_APP_START_KEY).collect { isFirstAppStart ->
                result = isFirstAppStart as Boolean? ?: true
            }
        }
        return result
    }

    fun getLanguageCode(): String {
        var result = String()
        viewModelScope.launch {
            dataStoreHelper.read(DataStoreHelper.LANGUAGE_CODE_KEY).collect { languageCode ->
                result = languageCode as String? ?: "en"
            }
        }
        return result
    }

    fun getUnitSystem(): String {
        var result = String()
        viewModelScope.launch {
            dataStoreHelper.read(DataStoreHelper.UNIT_SYSTEM_KEY).collect { unitSystem ->
                result = unitSystem as String? ?: "metric"
            }
        }
        return result
    }

    fun getSavedCityName(saveNameKey: String): String {
        var result = String()
        viewModelScope.launch {
            dataStoreHelper.readSavedCityName(saveNameKey).collect { savedCityName ->
                result = savedCityName ?: "none"
            }
        }
        return result
    }

    fun getFirstSaveName(): String {
        var result = String()
        viewModelScope.launch {
            dataStoreHelper.read(DataStoreHelper.FIRST_SAVE_NAME_KEY).collect { firstSaveName ->
                result = firstSaveName as String? ?: "none"
            }
        }
        return result
    }

    fun getSecondSaveName(): String {
        var result = String()
        viewModelScope.launch {
            dataStoreHelper.read(DataStoreHelper.SECOND_SAVE_NAME_KEY).collect { secondSaveName ->
                result = secondSaveName as String? ?: "none"
            }
        }
        return result
    }

    fun getThirdSaveName(): String {
        var result = String()
        viewModelScope.launch {
            dataStoreHelper.read(DataStoreHelper.THIRD_SAVE_NAME_KEY).collect { thirdSaveName ->
                result = thirdSaveName as String? ?: "none"
            }
        }
        return result
    }

    private suspend fun saveLastGeonameId(geoId: String) {
            dataStoreHelper.writeLastGeonameId(geoId)
    }

    fun saveLastLatitude(lastLatitude: Float) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeLastLatitude(lastLatitude)
        }
    }

    fun saveLastLongitude(lastLongitude: Float) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeLastLongitude(lastLongitude)
        }
    }

    fun saveLastSavedIsCoordinated(lastSavedIsCoordinated: Boolean) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeLastSavedIsCoordinated(lastSavedIsCoordinated)
        }
    }

    fun saveLanguageCode(languageCode: String) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeLanguageCode(languageCode)
        }
    }

    fun saveUnitSystem(unitSystem: String) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeUnitSystem(unitSystem)
        }
    }

    fun saveSavedCityName(savedCityNameId: String, savedCityName: String) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeSavedCityName(savedCityNameId, savedCityName)
        }
    }

    fun saveIsFirstAppStar(isFirstAppStart: Boolean) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeIsFirstAppStart(isFirstAppStart)
        }
    }

    //Necessary to stay in the index range of DAYS_OF_WEEK
    fun getCorrectIndex(day: Int): Int {
        return if (day <= 6) {
            day
        } else {
            day - 7
        }
    }

    fun getNextDaysInfo(forecastResponse: ForecastResponseModel): MutableList<NextDayInfoModel> {
        return getNextDaysInfoUseCase(forecastResponse)
    }

    fun getImageUrl(idIcon: String): String {
        return "https://openweathermap.org/img/wn/$idIcon@4x.png"
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}