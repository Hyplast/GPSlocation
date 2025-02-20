package fi.infinitygrow.gpslocation.domain.use_case

import fi.infinitygrow.gpslocation.domain.model.ForeCast
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository

class GetForecastInfoUseCase(private val weatherRepository: WeatherRepository) {

    suspend operator fun invoke(lat:Double, long:Double):Result<List<ForeCast>>{
        return try {
            val response = weatherRepository.getForecastInfo(lat, long)
            Result.success(response)
        }catch (e:Exception){
            Result.failure(e)
        }
    }

}