package fi.infinitygrow.gpslocation.domain.use_case

import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.RoadObservationData
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository

class GetRoadObservationUseCase(private val weatherRepository: WeatherRepository) {

    suspend operator fun invoke(lat:Double?, long:Double?, observationList: List<ObservationLocation>):Result<List<RoadObservationData>>{
        return try {
            val response = weatherRepository.getRoadObservation(lat, long, observationList)
            Result.success(response)
        }catch (e:Exception){
            Result.failure(e)
        }
    }

}