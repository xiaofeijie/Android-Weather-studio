package com.juanantbuit.weatherproject.domain.models.common

import com.google.gson.annotations.SerializedName

data class MainInfoModel (
    val temp: Double,
    @SerializedName("feels_like") val thermalSensation: Double,
    val humidity: Int
)
