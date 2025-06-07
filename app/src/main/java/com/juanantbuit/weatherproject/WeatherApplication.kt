package com.juanantbuit.weatherproject

import android.app.Application
import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class WeatherApplication : Application() {
    fun showToast(message: String?) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}