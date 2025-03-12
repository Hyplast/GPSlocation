package fi.infinitygrow.gpslocation.data.mapper

import fi.infinitygrow.gpslocation.data.database.StationEntity
import fi.infinitygrow.gpslocation.data.model.WeatherResponse
import fi.infinitygrow.gpslocation.data.model.forecast.ForecastResponse
import fi.infinitygrow.gpslocation.domain.model.ForeCast
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.Weather
import fi.infinitygrow.gpslocation.domain.model.getObservationLocationByName


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

fun extractTemperature(html: String): String? {
    val regex = """Temperature: (\d+\.?\d*)°C""".toRegex()
    return regex.find(html)?.groups?.get(1)?.value
}


// Mappers.kt

// Convert from ObservationLocation to StationEntity
fun ObservationLocation.toStationEntity(): StationEntity {
    // the url might be constructed based on some known pattern or can be empty
    // if not needed. Adjust as necessary.
    return StationEntity(
        name = this.name,
        fmisid = this.fmiId,
        latitude = this.latitude,
        longitude = this.longitude,
        url = "" // assign a proper value or derive one, if necessary.
    )
}

fun StationEntity.toObservationLocation(): ObservationLocation? {
    return getObservationLocationByName(this.name)
}


// Convert from StationEntity to ObservationLocation
//fun StationEntity.toObservationLocation(): ObservationLocation {
//    // When mapping back, we might not have all details available.
//    // Hence, default values or nulls may be used for missing properties.
//    return ObservationLocation(
//        name = this.name,
//        fmiId = this.fmisid,
//        lpnnId = null,
//        wmoId = null,
//        latitude = this.latitude,
//        longitude = this.longitude,
//        altitude = 0,  // Default, unless you have some mapping logic.
//        type = "",     // Default or derived value.
//        year = null    // Default value.
//    )
//}


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