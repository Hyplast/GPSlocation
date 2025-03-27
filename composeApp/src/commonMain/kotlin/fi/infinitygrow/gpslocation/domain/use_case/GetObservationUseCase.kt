package fi.infinitygrow.gpslocation.domain.use_case

import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository

class GetObservationUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(lat: Double?, long: Double?, observationList: List<ObservationLocation>): Result<List<ObservationData>> {
        return Result.success(weatherRepository.getObservation(lat, long, observationList))
    }
}