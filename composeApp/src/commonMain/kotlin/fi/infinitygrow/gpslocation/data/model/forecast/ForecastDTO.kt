package fi.infinitygrow.gpslocation.data.model.forecast

import fi.infinitygrow.gpslocation.data.model.MainDTO
import fi.infinitygrow.gpslocation.data.model.WeatherDTO
import kotlinx.serialization.Serializable

@Serializable
data class ForecastDTO(
    val dt: Int,
    val main: MainDTO,
    val weather: List<WeatherDTO>,
)
