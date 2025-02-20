package fi.infinitygrow.gpslocation.data.model.forecast

import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val list: List<ForecastDTO>,
)