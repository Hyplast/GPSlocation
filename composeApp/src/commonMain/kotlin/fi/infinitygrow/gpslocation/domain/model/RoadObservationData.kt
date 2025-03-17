package fi.infinitygrow.gpslocation.domain.model

import fi.infinitygrow.gpslocation.presentation.permission.Location

data class RoadObservationData(
        val name: String,
        val coordinates: Location,
        val longitude: Double,
        val latitude: Double,
        val unixTime: Long,
        val temperature: Double,
        val windSpeed: Double,
        val windGust: Double,
        val windDirection: Double,
        val humidity: Double,
        val dewPoint: Double,
        val precipitationAmount: Double,
        val precipitationIntensity: Double,
        val snowDepth: Double,
        val pressure: Double,
        val visibility: Double,
        val cloudAmount: Double,
        val presentWeather: Double
    )