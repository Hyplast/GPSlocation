package fi.infinitygrow.gpslocation.presentation.utils

import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.presentation.permission.Location
import kotlin.math.roundToInt


//fun constructLanguageString(data: ObservationData?, location: Location): String? {
//    if (data == null) {
//        return null
//    }
//    val dist = getDistance(data.latitude, data.longitude, location.latitude, location.longitude)
//    val bear = getBearing(location.latitude, location.longitude, data.latitude, data.longitude)
//    return "Uusi havainto. Etäisyys ${dist.roundToInt()} kilometriä ${bearingToDirection(bear)}." +
//            data.precipitationIntensity.let { if (it == 0.0) "" else " sadetta $it millimetriä tunnissa" } +
//            data.windSpeed.let { " Tuuli ${it.roundToInt()}" } +
//            data.windGust.let { " kautta ${it.roundToInt()} metriä sekunnissa. " } +
//            data.windDirection.let { " Suunta ${it.roundToNearestFive().toString()} astetta. " } +
//            " Pilvenpohjat ${calculateCloudBaseHeight(data.temperature,data.dewPoint,0.0).roundToNearestHundred()} metriä."
//}

fun constructLanguageString(data: ObservationData?, location: Location): String? {
    if (data == null) return null

    println("Construct language string.")
    println(data.latitude)
    println(data.longitude)
    println(location.latitude)
    println(location.longitude)

    val dist = getDistance(data.longitude, data.latitude, location.longitude, location.latitude)
        .takeIf { it.isFinite() }?.roundToInt()

    val bear = getBearing(data.latitude, data.longitude, location.latitude, location.longitude)
        .takeIf { it.isFinite() }?.let { bearingToDirection(it) }

    val parts = mutableListOf<String>()

    parts.add("Uusi havainto.")
    dist?.let { parts.add("Etäisyys $it kilometriä") }
    bear?.let { parts.add(it) }

    data.precipitationIntensity.takeIf { it.isFinite() && it != 0.0 }?.let {
        parts.add("sadetta $it millimetriä tunnissa")
    }

    data.windSpeed.takeIf { it.isFinite() }?.roundToInt()?.let {
        parts.add("Tuuli $it")
    }

    data.windGust.takeIf { it.isFinite() }?.roundToInt()?.let {
        parts.add("kautta $it metriä sekunnissa.")
    }

    data.windDirection.takeIf { it.isFinite() }?.roundToNearestFive()?.let {
        parts.add("Suunta $it astetta.")
    }

    calculateCloudBaseHeight(data.temperature, data.dewPoint, 0.0)
        .takeIf { it.isFinite() }
        ?.roundToNearestHundred()
        ?.let { parts.add("Pilvenpohjat $it metriä.") }

    data.presentWeather.takeIf { it.isFinite() }?.let {
        parts.add(getWeatherDescription(it.toInt()))
    }

    return parts.joinToString(" ")
}


private fun Double?.roundToNearestFive(): Int? {
    val remainder = this?.rem(5)
    val roundedDegrees = if (remainder!! < 2.5) {
        this?.minus(remainder)
    } else {
        this?.plus((5 - remainder))
    }
    if (roundedDegrees != null) {
        return roundedDegrees.roundToInt()
    }
    return null
}

private fun Double?.roundToNearestHundred(): Int? {
    val remainder = this?.rem(100)
    val roundedDegrees = if (remainder!! < 50) {
        this?.minus(remainder)
    } else {
        this?.plus((100 - remainder))
    }
    if (roundedDegrees != null) {
        return roundedDegrees.roundToInt()
    }
    return null
}

fun getWeatherDescription(code: Int): String {
    // Remove first digit if it's 1 and the code is 3 digits
    val normalizedCode = if (code >= 100 && code < 200) code % 100 else code

    return when (normalizedCode) {
        0 -> "No significant weather observed"
        1 -> "Clouds generally dissolving or becoming less developed"
        2 -> "State of sky on the whole unchanged"
        3 -> "Clouds generally forming or developing"
        4 -> "Haze or smoke, or dust in suspension in the air, visibility >= 1 km"
        5 -> "Haze or smoke, or dust in suspension in the air, visibility < 1 km"
        10 -> "Mist"
        11 -> "Diamond dust"
        12 -> "Distant lightning"
        18 -> "Squalls"
        20 -> "Fog (preceding hour)"
        21 -> "Precipitation (preceding hour)"
        22 -> "Drizzle or snow grains (preceding hour)"
        23 -> "Rain (preceding hour)"
        24 -> "Snow (preceding hour)"
        25 -> "Freezing drizzle or freezing rain (preceding hour)"
        26 -> "Thunderstorm (preceding hour)"
        27 -> "Blowing or drifting snow or sand"
        28 -> "Blowing or drifting snow or sand, visibility >= 1 km"
        29 -> "Blowing or drifting snow or sand, visibility < 1 km"
        30 -> "Fog"
        31 -> "Fog or ice fog in patches"
        32 -> "Fog or ice fog, becoming thinner"
        33 -> "Fog or ice fog, no change"
        34 -> "Fog or ice fog, becoming thicker"
        35 -> "Fog, depositing rime"
        40 -> "Precipitation"
        41 -> "Slight or moderate precipitation"
        42 -> "Heavy precipitation"
        43 -> "Slight or moderate liquid precipitation"
        44 -> "Heavy liquid precipitation"
        45 -> "Slight or moderate solid precipitation"
        46 -> "Heavy solid precipitation"
        47 -> "Slight or moderate freezing precipitation"
        48 -> "Heavy freezing precipitation"
        50 -> "Drizzle"
        51 -> "Slight drizzle"
        52 -> "Moderate drizzle"
        53 -> "Heavy drizzle"
        54 -> "Slight freezing drizzle"
        55 -> "Moderate freezing drizzle"
        56 -> "Heavy freezing drizzle"
        57 -> "Slight drizzle and rain"
        58 -> "Moderate or heavy drizzle and rain"
        60 -> "Rain"
        61 -> "Slight rain"
        62 -> "Moderate rain"
        63 -> "Heavy rain"
        64 -> "Slight freezing rain"
        65 -> "Moderate freezing rain"
        66 -> "Heavy freezing rain"
        67 -> "Slight rain and snow"
        68 -> "Heavy rain and snow"
        70 -> "Snow"
        71 -> "Slight snow"
        72 -> "Moderate snow"
        73 -> "Heavy snow"
        74 -> "Slight ice pellets"
        75 -> "Moderate ice pellets"
        76 -> "Heavy ice pellets"
        77 -> "Snow grains"
        78 -> "Ice crystals"
        80 -> "Showers or intermittent precipitation"
        81 -> "Slight rain showers"
        82 -> "Moderate rain showers"
        83 -> "Heavy rain showers"
        84 -> "Violent rain showers"
        85 -> "Slight snow showers"
        86 -> "Moderate snow showers"
        87 -> "Heavy snow showers"
        89 -> "Hail"
        90 -> "Thunderstorm"
        91 -> "Slight/moderate thunderstorm, no precipitation"
        92 -> "Slight/moderate thunderstorm with rain/snow"
        93 -> "Slight/moderate thunderstorm with hail"
        94 -> "Heavy thunderstorm, no precipitation"
        95 -> "Heavy thunderstorm with rain/snow"
        96 -> "Heavy thunderstorm with hail"
        99 -> "Tornado"
        else -> "Unknown weather code"
    }
}