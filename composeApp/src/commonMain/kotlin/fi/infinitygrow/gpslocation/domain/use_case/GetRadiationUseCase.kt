package fi.infinitygrow.gpslocation.domain.use_case

import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.RadiationData
import fi.infinitygrow.gpslocation.domain.model.RoadObservationData
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository

class GetRadiationUseCase(private val weatherRepository: WeatherRepository) {

    suspend operator fun invoke(lat:Double?, long:Double?):Result<List<RadiationData>>{
        return try {
            val response = weatherRepository.getSunRadiation(lat, long)
            Result.success(response)
        }catch (e:Exception){
            Result.failure(e)
        }
    }

}