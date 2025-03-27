package fi.infinitygrow.gpslocation.domain.use_case

import fi.infinitygrow.gpslocation.domain.model.SoundingData
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository

class GetSoundingUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(lat:Double?, long:Double?):Result<List<SoundingData>>{
        return Result.success(weatherRepository.getSounding(lat, long))
    }
}