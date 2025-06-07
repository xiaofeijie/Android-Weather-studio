package com.juanantbuit.weatherproject.utils

import com.juanantbuit.weatherproject.BuildConfig

const val BASE_URL: String = "https://api.openweathermap.org/"
const val API_KEY: String = BuildConfig.OPENWEATHER_KEY
var UNITS: String = "metric"
var LANG: String = "en"
const val TRI_HOURS_IN_DAY: Int = 8
const val AFTERNOON_TIME_INDEX: Int = 4
const val GPS_REQUEST_CODE = 1