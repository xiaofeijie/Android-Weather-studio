package com.juanantbuit.weatherproject.domain.models

import com.google.gson.annotations.SerializedName

data class ForecastResponseModel (
    @SerializedName("list") val forecastInfoModels: List<ForecastInfoModel>
)
