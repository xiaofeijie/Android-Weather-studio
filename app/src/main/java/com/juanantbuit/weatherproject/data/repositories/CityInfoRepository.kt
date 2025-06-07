package com.juanantbuit.weatherproject.data.repositories

import com.juanantbuit.weatherproject.data.datasources.openweather.CityInfoService
import com.juanantbuit.weatherproject.domain.models.CityInfoModel
import javax.inject.Inject

class CityInfoRepository @Inject constructor(
    private val api: CityInfoService
) {
    suspend fun getCityInfo(latitude: Float, longitude: Float): CityInfoModel {
        return api.getCityInfo(latitude, longitude)
    }

    suspend fun getCityInfo(geoId: String): CityInfoModel? {
        return api.getCityInfo(geoId)
    }
}