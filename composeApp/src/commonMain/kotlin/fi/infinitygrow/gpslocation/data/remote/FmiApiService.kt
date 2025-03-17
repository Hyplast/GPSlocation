package fi.infinitygrow.gpslocation.data.remote

import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.RadiationData
import fi.infinitygrow.gpslocation.domain.model.RoadObservationData
import fi.infinitygrow.gpslocation.domain.model.SoundingData

interface FmiApiService {
    suspend fun observation(
        longitude: Double?,
        latitude: Double?,
        location: Boolean,
        radiusKm: Int?,
        observationList: List<ObservationLocation>
    ): List<ObservationData>

    suspend fun roadObservation(
        longitude: Double?,
        latitude: Double?,
        location: Boolean,
        radiusKm: Int?,
        observationList: List<ObservationLocation>
    ): List<RoadObservationData>

    suspend fun sunRadiation(
        longitude: Double,
        latitude: Double
    ): List<RadiationData>

    suspend fun lightningStrikes(
        longitude: Double,
        latitude: Double
    )

    suspend fun getSounding(): List<SoundingData>

}
