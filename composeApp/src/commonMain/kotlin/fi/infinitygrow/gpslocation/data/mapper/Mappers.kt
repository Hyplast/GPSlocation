package fi.infinitygrow.gpslocation.data.mapper

import fi.infinitygrow.gpslocation.data.model.WeatherResponse
import fi.infinitygrow.gpslocation.data.model.forecast.ForecastResponse
import fi.infinitygrow.gpslocation.data.model.observation.ObservationDTO
import fi.infinitygrow.gpslocation.domain.model.ForeCast
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.Weather


fun WeatherResponse.toDomain(): Weather {
    return Weather(
        name= name,
        temperature = this.main.temp.minus(273).toString(),
        iconUrl = getImageUrl(this.weather.first().icon)
    )
}

fun ForecastResponse.toDomain():List<ForeCast>{
    return this.list.map {
        ForeCast(
            date = formatDate(it.dt.toLong()),
            temperature = it.main.temp.minus(273).toString(),
            iconUrl = getImageUrl(it.weather.first().icon)
        )
    }
}

//fun ObservationDTO.toDomain():List<ObservationData>{
//    return this.list.map {
//        ForeCast(
//            date = formatDate(it.dt.toLong()),
//            temperature = it.main.temp.minus(273).toString(),
//            iconUrl = getImageUrl(it.weather.first().icon)
//        )
//    }
//}

//class ObservationResponse {
//
//    val list: Any
//}


expect fun formatDate(millis:Long):String

fun getImageUrl(iconId:String)="https://openweathermap.org/img/wn/$iconId@2x.png"