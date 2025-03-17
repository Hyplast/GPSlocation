package fi.infinitygrow.gpslocation.domain.model

import fi.infinitygrow.gpslocation.presentation.permission.Location

data class SoundingData(
    val name: String,
    val coordinates: Location,
    val longitude: Double,
    val latitude: Double,
    val altitude: Double,
    val unixTime: Long,
    val temperature: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val humidity: Double,
    val dewPoint: Double,
)