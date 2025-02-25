package fi.infinitygrow.gpslocation.data.remote

import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation

interface FmiApiService {
    suspend fun observation(
        longitude: Double,
        latitude: Double,
        observationList: List<ObservationLocation>
    ): List<ObservationData>

    suspend fun sunRadiation(
        longitude: Double,
        latitude: Double
    )

    suspend fun lightningStrikes(
        longitude: Double,
        latitude: Double
    )

}
