package com.juanantbuit.weatherproject.framework.ui.searchList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.search.client.Index
import com.algolia.search.dsl.query
import com.algolia.search.model.response.ResponseSearch
import com.algolia.search.model.settings.Settings
import com.google.gson.Gson
import com.juanantbuit.weatherproject.domain.models.ItemModel
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import com.juanantbuit.weatherproject.framework.helpers.DataStoreHelper
import com.juanantbuit.weatherproject.usecases.GetCityInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchListViewModel @Inject constructor(
    private val getCityInfoUseCase: GetCityInfoUseCase,
    private val dataStoreHelper: DataStoreHelper,
    private val index: Index,
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchListUIState>(SearchListUIState.NotFound)
    val uiState: StateFlow<SearchListUIState> = _uiState

    fun initSearchSettings() {
        viewModelScope.launch {
            index.setSettings(settings)
        }
    }

    fun getSearchItemList(searchQuery: String) {
        viewModelScope.launch {
            _uiState.value = SearchListUIState.Loading
            val result: MutableList<SearchItemModel> = arrayListOf()
            val query = query(searchQuery) {
                page = 0
            }

            val responseSearch: ResponseSearch = coroutineScope {
                index.search(query)
            }

            if (responseSearch.nbHits != 0) {
                responseSearch.hits.forEach { hit ->
                    result.add(
                        Gson().fromJson(hit.json.toString(), SearchItemModel::class.java)
                    )
                }
                val cityInfoList = withContext(dispatcher) {
                    getCityInfoUseCase.getCityInfoList(result)
                }
                val itemModelsLists = ItemModel(result, cityInfoList)

                _uiState.value = SearchListUIState.Success(itemModelsLists)
            } else {
                _uiState.value = SearchListUIState.NotFound
            }
        }
    }

    fun setLoading() {
        _uiState.value = SearchListUIState.Loading
    }

    fun setNotFound() {
        _uiState.value = SearchListUIState.NotFound
    }

    fun saveFirstSaveName(firstSaveName: String) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeFirstSaveName(firstSaveName)
        }
    }

    fun saveSecondSaveName(secondSaveName: String) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeSecondSaveName(secondSaveName)
        }
    }

    fun saveThirdSaveName(thirdSaveName: String) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeThirdSaveName(thirdSaveName)
        }
    }

    fun saveLastGeonameId(geoId: String) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeLastGeonameId(geoId)
        }
    }

    fun saveGeonameId(geoIdKey: String, geoId: String) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeGeonameId(geoIdKey, geoId)
        }
    }

    fun saveLastSavedIsCoordinated(lastSavedIsCoordinated: Boolean) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeLastSavedIsCoordinated(lastSavedIsCoordinated)
        }
    }

    fun saveIsFirstAppStar(isFirstAppStart: Boolean) {
        viewModelScope.launch(dispatcher) {
            dataStoreHelper.writeIsFirstAppStart(isFirstAppStart)
        }
    }
}
