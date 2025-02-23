package fi.infinitygrow.gpslocation.data.remote

import fi.infinitygrow.gpslocation.domain.model.ObservationData

interface FmiApiService {
    suspend fun observation(
        longitude: Double,
        latitude: Double
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
