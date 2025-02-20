package fi.infinitygrow.gpslocation.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MainDTO(
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)