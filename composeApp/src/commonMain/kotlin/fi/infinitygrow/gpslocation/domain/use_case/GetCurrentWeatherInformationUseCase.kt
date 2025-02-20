package fi.infinitygrow.gpslocation.domain.use_case

import fi.infinitygrow.gpslocation.domain.model.Weather
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository

class GetCurrentWeatherInfoUseCase (private val weatherRepository: WeatherRepository){


    suspend operator fun invoke(lat:Double,long:Double) : Result<Weather>{
        return try {
            val response = weatherRepository.getCurrentWeatherInfo(lat, long)
            Result.success(response)
        }catch (e:Exception){
            Result.failure(e)
        }
    }

}