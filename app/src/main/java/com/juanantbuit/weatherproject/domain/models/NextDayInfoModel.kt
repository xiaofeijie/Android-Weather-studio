package com.juanantbuit.weatherproject.domain.models

data class NextDayInfoModel(
    val iconId: String,
    val temperatures: MutableList<Double>,
    val averageTemp: Int,
    val lowestTemp: Int,
    val highestTemp: Int
)