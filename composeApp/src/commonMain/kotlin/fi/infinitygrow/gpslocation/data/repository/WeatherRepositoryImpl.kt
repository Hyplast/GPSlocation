package fi.infinitygrow.gpslocation.data.repository

import fi.infinitygrow.gpslocation.data.mapper.toDomain
import fi.infinitygrow.gpslocation.data.remote.ApiService
import fi.infinitygrow.gpslocation.data.remote.FmiApiService
import fi.infinitygrow.gpslocation.domain.model.ForeCast
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.RadiationData
import fi.infinitygrow.gpslocation.domain.model.RoadObservationData
import fi.infinitygrow.gpslocation.domain.model.SoundingData
import fi.infinitygrow.gpslocation.domain.model.Weather
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.first

class WeatherRepositoryImpl(
    private val apiService: ApiService,
    private val fmiApiService: FmiApiService,
    private val settingsRepository: SettingsRepository
) : WeatherRepository {
    override suspend fun getCurrentWeatherInfo(lat: Double, long: Double): Weather {
        return apiService.currentWeatherInfo(lat, long).toDomain()
    }

    override suspend fun getForecastInfo(lat: Double, long: Double): List<ForeCast> {
        return apiService.forecastInfo(lat, long).toDomain()
    }

    override suspend fun getObservation(latitude: Double?, longitude: Double?, observationList: List<ObservationLocation>): List<ObservationData> {
        val radius = settingsRepository.radiusFlow.first()
        val location = settingsRepository.locationFlow.first()
        return fmiApiService.observation(latitude, longitude, location, radius, observationList)//.toDomain()
    }

    override suspend fun getRoadObservation(latitude: Double?, longitude: Double?, observationList: List<ObservationLocation>): List<RoadObservationData> {
        val radius = settingsRepository.radiusFlow.first()
        val location = settingsRepository.locationFlow.first()
        return fmiApiService.roadObservation(latitude, longitude, location, radius, observationList)//.toDomain()
    }

    override suspend fun getSunRadiation(latitude: Double?, longitude: Double?): List<RadiationData> {
        return fmiApiService.sunRadiation(latitude, longitude)
    }

    override suspend fun getLightningStrikes(latitude: Double, longitude: Double): String {
        TODO("Not yet implemented")
    }

    override suspend fun getSounding(latitude: Double?, longitude: Double?): List<SoundingData> {
        return fmiApiService.getSounding(latitude, longitude)
    }

    override suspend fun getSoilTemperature(
        latitude: Double,
        longitude: Double
    ): List<ObservationData> {
        TODO("Not yet implemented")
    }
}
