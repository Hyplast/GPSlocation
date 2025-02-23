package fi.infinitygrow.gpslocation.data.repository

import fi.infinitygrow.gpslocation.data.mapper.toDomain
import fi.infinitygrow.gpslocation.data.remote.ApiService
import fi.infinitygrow.gpslocation.data.remote.FmiApiService
import fi.infinitygrow.gpslocation.domain.model.ForeCast
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.Weather
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val apiService: ApiService,
    private val fmiApiService: FmiApiService
) : WeatherRepository {
    override suspend fun getCurrentWeatherInfo(lat: Double, long: Double): Weather {
        return apiService.currentWeatherInfo(lat, long).toDomain()
    }

    override suspend fun getForecastInfo(lat: Double, long: Double): List<ForeCast> {
        return apiService.forecastInfo(lat, long).toDomain()
    }

    override suspend fun getObservation(latitude: Double, longitude: Double): List<ObservationData> {
        return fmiApiService.observation(latitude, longitude)//.toDomain()
    }

    override suspend fun getSunRadiation(latitude: Double, longitude: Double): String {
        TODO("Not yet implemented")
    }

    override suspend fun getLightningStrikes(latitude: Double, longitude: Double): String {
        TODO("Not yet implemented")
    }


}

//
//class FmiWeatherRepository(private val fmiApiService: FmiApiService) : FmiWeatherRepository {
//    override suspend fun getObservation(latitude: Double, longitude: Double): List<ObservationData> {
//        return fmiApiService.
//    }
//}