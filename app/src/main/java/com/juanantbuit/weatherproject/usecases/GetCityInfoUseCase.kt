package com.juanantbuit.weatherproject.usecases

import com.juanantbuit.weatherproject.data.repositories.CityInfoRepository
import com.juanantbuit.weatherproject.domain.models.CityInfoModel
import com.juanantbuit.weatherproject.domain.models.SearchItemModel
import javax.inject.Inject

class GetCityInfoUseCase @Inject constructor(
    private val repository: CityInfoRepository
) {
    suspend fun getCityInfo(latitude: Float, longitude: Float): CityInfoModel =
        repository.getCityInfo(latitude, longitude)

    suspend fun getCityInfo(geoId: String): CityInfoModel? = repository.getCityInfo(geoId)
    suspend fun getCityInfoList(searchItemList: List<SearchItemModel>): List<CityInfoModel> {
        val cityInfoList: MutableList<CityInfoModel> = arrayListOf()
        searchItemList.forEach { searchItem ->
            getCityInfo(searchItem.geonameid)?.let { cityInfoList.add(it) }
        }
        return cityInfoList
    }
}
