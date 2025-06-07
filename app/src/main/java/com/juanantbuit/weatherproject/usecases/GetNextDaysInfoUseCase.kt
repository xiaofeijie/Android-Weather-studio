package com.juanantbuit.weatherproject.usecases

import com.juanantbuit.weatherproject.domain.models.ForecastInfoModel
import com.juanantbuit.weatherproject.domain.models.ForecastResponseModel
import com.juanantbuit.weatherproject.domain.models.NextDayInfoModel
import com.juanantbuit.weatherproject.utils.AFTERNOON_TIME_INDEX
import com.juanantbuit.weatherproject.utils.TRI_HOURS_IN_DAY
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetNextDaysInfoUseCase @Inject constructor() {
    operator fun invoke(forecastResponse: ForecastResponseModel): MutableList<NextDayInfoModel> {
        val forecastsForFiveDays: List<List<ForecastInfoModel>> =
            getOnlyNextDaysInfo(forecastResponse).chunked(TRI_HOURS_IN_DAY)
        val nextDaysInfo: MutableList<NextDayInfoModel> = arrayListOf()

        for (day in 0..3) {

            val dayTemps = getAllTempsOfDay(forecastsForFiveDays, day)
            val averageTemp = dayTemps.average().toInt()
            val lowestTemp = dayTemps.min().toInt()
            val highestTemp = dayTemps.max().toInt()

            nextDaysInfo.add(
                NextDayInfoModel(
                    forecastsForFiveDays[day][AFTERNOON_TIME_INDEX].iconId[0].idIcon,
                    dayTemps,
                    averageTemp,
                    lowestTemp,
                    highestTemp
                )
            )
        }
        return nextDaysInfo
    }

    private fun getOnlyNextDaysInfo(forecastResponse: ForecastResponseModel): MutableList<ForecastInfoModel> {
        val nextDaysForecast: MutableList<ForecastInfoModel> = ArrayList()
        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (i in 0 until forecastResponse.forecastInfoModels.size) {
            val nextDate = LocalDate.parse(
                forecastResponse.forecastInfoModels[i].date.substringBefore(" "), formatter
            )

            if (current != nextDate) {
                nextDaysForecast.add(forecastResponse.forecastInfoModels[i])
            }
        }
        return nextDaysForecast
    }

    private fun getAllTempsOfDay(
        forecastsForFiveDays: List<List<ForecastInfoModel>>, day: Int
    ): MutableList<Double> {
        val dayTemps: MutableList<Double> = arrayListOf()

        for (triHourlyForecast in forecastsForFiveDays[day].indices) {
            dayTemps.add(forecastsForFiveDays[day][triHourlyForecast].mainInfo.temp)
        }
        return dayTemps
    }
}
