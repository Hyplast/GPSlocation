package fi.infinitygrow.gpslocation.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherDTO(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)