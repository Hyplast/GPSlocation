package fi.infinitygrow.gpslocation.domain.use_case

import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.RadiationData
import fi.infinitygrow.gpslocation.domain.model.RoadObservationData
import fi.infinitygrow.gpslocation.domain.model.SoundingData
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository

class GetSoundingUseCase(private val weatherRepository: WeatherRepository) {

    suspend operator fun invoke(lat:Double?, long:Double?):Result<List<SoundingData>>{
        return try {
            val response = weatherRepository.getSounding(lat, long)
            Result.success(response)
        }catch (e:Exception){
            Result.failure(e)
        }
    }

}