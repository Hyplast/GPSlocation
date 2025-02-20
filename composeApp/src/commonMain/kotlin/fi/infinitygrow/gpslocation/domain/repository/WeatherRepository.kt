package fi.infinitygrow.gpslocation.domain.repository

import fi.infinitygrow.gpslocation.domain.model.ForeCast
import fi.infinitygrow.gpslocation.domain.model.Weather

interface WeatherRepository {

    suspend fun getCurrentWeatherInfo(lat: Double, long: Double): Weather

    suspend fun getForecastInfo(lat: Double, long: Double): List<ForeCast>

}