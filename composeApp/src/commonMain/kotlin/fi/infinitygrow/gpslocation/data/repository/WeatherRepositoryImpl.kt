package fi.infinitygrow.gpslocation.data.repository

import fi.infinitygrow.gpslocation.data.mapper.toDomain
import fi.infinitygrow.gpslocation.data.remote.ApiService
import fi.infinitygrow.gpslocation.domain.model.ForeCast
import fi.infinitygrow.gpslocation.domain.model.Weather
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository

class WeatherRepositoryImpl(private val apiService: ApiService) : WeatherRepository {
    override suspend fun getCurrentWeatherInfo(lat: Double, long: Double): Weather {
        return apiService.currentWeatherInfo(lat, long).toDomain()
    }

    override suspend fun getForecastInfo(lat: Double, long: Double): List<ForeCast> {
        return apiService.forecastInfo(lat, long).toDomain()
    }
}