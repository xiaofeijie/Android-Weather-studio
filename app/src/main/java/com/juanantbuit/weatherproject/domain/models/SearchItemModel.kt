package com.juanantbuit.weatherproject.domain.models

data class SearchItemModel(
    val name: String,
    val country: String,
    val subcountry: String,
    val geonameid: String
)
