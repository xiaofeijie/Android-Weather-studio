package com.juanantbuit.weatherproject.domain.models

import com.google.gson.annotations.SerializedName
import com.juanantbuit.weatherproject.domain.models.common.IconIdModel
import com.juanantbuit.weatherproject.domain.models.common.MainInfoModel

data class ForecastInfoModel (
    @SerializedName("main") val mainInfo: MainInfoModel,
    @SerializedName("weather") val iconId: List<IconIdModel>, //The list is to be able to parse the JSON correctly
    @SerializedName("dt_txt") val date: String
)
