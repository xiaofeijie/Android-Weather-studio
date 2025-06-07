package com.juanantbuit.weatherproject.data.repositories

import com.juanantbuit.weatherproject.data.datasources.openweather.ForecastResponseService
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel
import javax.inject.Inject

class ForecastResponseRepository @Inject constructor(
    private val api: ForecastResponseService
){

    suspend fun getForecastResponse(latitude: Float, longitude: Float): ForecastResponseModel {
        return api.getForecastResponse(latitude, longitude)
    }

    suspend fun getForecastResponse(geoId: String): ForecastResponseModel {
        return api.getForecastResponse(geoId)
    }
}
