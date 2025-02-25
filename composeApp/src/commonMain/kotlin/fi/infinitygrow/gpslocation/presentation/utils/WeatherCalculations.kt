package fi.infinitygrow.gpslocation.presentation.utils

import kotlin.math.exp
import kotlin.math.log
import kotlin.math.pow

fun calculateCloudBaseHeight(temperatureC: Double, dewPointC: Double, heightStationM: Double): Double {
    return ((temperatureC - dewPointC) / 10) * 1247 + heightStationM
}

private const val Tb = 288 // Standard temperature at sea level in Kelvin
private const val Lb = -0.0065 // Standard temperature lapse rate in Kelvin per meter
private const val R = 8.31432 // Universal gas constant in J/(mol*K)
private const val g0 = 9.80665 // Standard gravitational acceleration in m/s^2
private const val M = 0.0289644  // Molar mass of dry air in kg/mol
private const val lapseRate = 0.0065 // Standard lapse rate in K/m

/**
 * Calculates altitude from air pressure and temperature.
 *
 * @param pressure The air pressure in Pascals (Pa).
 * @param temperature The temperature in Kelvin (K).
 * @param pressureAtSeaLevel The air pressure at sea level in Pascals (Pa).
 * @return The altitude in meters (m).
 */
fun altitudeFromPressure(pressure: Double, temperature: Double, pressureAtSeaLevel: Double = 101325.0): Double {
    val h1 = 0.0 // Altitude at sea level

    return h1 + (temperature / -0.0065) *  ((pressure / pressureAtSeaLevel).pow((-8.31432*(-0.0065))/(9.80665*0.0289644)) - 1) // Calculate altitude using the barometric formula
}

/**
 * Calculates air pressure at a given altitude, adjusting for the measurement station's altitude.
 *
 * @param altitude The altitude in meters (m) where we want to determine pressure.
 * @param temperatureAtStation The temperature at the measurement station in Kelvin (K).
 * @param pressureAtSeaLevel The standardized air pressure at sea level in Pascals (Pa).
 * @param heightStationM The altitude of the weather station in meters (m).
 * @return The air pressure in Pascals (Pa).
 */
fun pressureFromAltitude(
    altitude: Double,
    temperatureAtStation: Double,
    pressureAtSeaLevel: Double = 101325.0,
    heightStationM: Double = 0.0
): Double {
    val temperatureAtAltitude = calcTemperatureAtAltitude(altitude, heightStationM, temperatureAtStation)
    val altitudeDiff = altitude - heightStationM

    return pressureAtSeaLevel * exp((-g0 * M * altitudeDiff) / (R * temperatureAtStation))//temperatureAtAltitude))
}

fun calcTemperatureAtAltitude(altitude: Double, measurementAltitude: Double, measuredTemperature: Double): Double {
    return measuredTemperature - (lapseRate * (altitude - measurementAltitude))
}
//fun pressureFromAltitude(
//    altitude: Double,
//    temperatureAtStation: Double,
//    pressureAtSeaLevel: Double = 101325.0,
//    heightStationM: Double = 0.0
//): Double {
//    //val temperatureAtAltitude = temperatureAtStation - (lapseRate * (altitude - heightStationM))
//    val temperatureAtAltitude = calcTemperatureAtAltitude(altitude, heightStationM, temperatureAtStation)
//
//    return pressureAtSeaLevel * exp((-g0 * M * (altitude)) / (R * temperatureAtAltitude))
//}

//fun calcTemperatureAtAltitude(altitude: Double, measurementAltitude: Double, measuredTemperature: Double): Double {
//    return measuredTemperature - (lapseRate * (altitude - measurementAltitude))
//}

//fun pressureFromAltitude(
//    altitude: Double,
//    temperature: Double,
//    pressureAtSeaLevel: Double = 101325.0
//): Double {
//    return pressureAtSeaLevel * exp((-g0 * M * altitude) / (R * temperature))
//}
//
//fun pressureFromAltitude(altitude: Double, temperature: Double, pressureAtSeaLevel: Double = 101325.0, heightStationM: Double = 0.0): Double {
//    return pressureAtSeaLevel * (1 - 0.0065/temperature * (altitude - 0)).pow((-9.80665*0.0289644)/(8.31432*(-0.0065)))
//}

fun calcSeaLevelTemperature(temperatureAtAltitude: Double, altitude: Double): Double {
    return temperatureAtAltitude + (lapseRate * altitude)
}

const val pDefault = 101325.0 // Default pressure in Pascals
const val tDefault = 288.15 // Default temperature in Kelvin

fun altcalc(a: Double, k: Double, i: Double): Double {
    return when {
        (a / i) < (pDefault / 22632.1) -> {
            val d = -0.0065
            val e = 0.0
            val j = (i / a).pow((R * d) / (g0 * M))
            e + (k * ((1 / j) - 1) / d)
        }
        (a / i) < (pDefault / 5474.89) -> {
            val e = 11000.0
            val b = k - 71.5
            val f = (R * b * log(i, a)) / (-g0 * M)
            val l = pDefault
            val c = 22632.1
            val h = (R * b * log(l, c)) / (-g0 * M) + e
            h + f
        }
        else -> Double.NaN
    }
}

fun pressTempAlt(b: Double, k: Double, j: Double): Double {
    return when {
        j < 11000 -> {
            val e = -0.0065
            val i = 0.0
            b * (k / (k + (e * (j - i)))).pow((g0 * M) / (R * e))
        }
        j <= 20000 -> {
            val e = -0.0065
            val i = 0.0
            val f = 11000.0
            val a = b * (k / (k + (e * (f - i)))).pow((g0 * M) / (R * e))
            val c = k + (11000 * e)
            val d = 0.0
            a * exp((-g0 * M * (j - f)) / (R * c))
        }
        else -> Double.NaN
    }
}

fun pressureTemperatureAltitude(p: Double, t: Double, a: Double): Double {
    val one = pressTempAlt(p,t,a)
    val two = altcalc(p,t,one)
    return two
}


/**
 * Calculates altitude from given temperature and altitude in feet and then uses the result air pressure with `altitudeFromPressure()` with same temperature and given pressure at sea level to calculate final altitude in meters.
 *
 * @param temperature The temperature in Kelvin (K).
 * @param altitudeInMeters The altitude in meters (m).
 * @param pressureAtSeaLevel The air pressure at sea level in Pascals (Pa).
 * @return The final altitude in meters (m).
 */
fun calculateAltitude(
    temperature: Double,
    altitudeInMeters: Double,
    pressureAtSeaLevel: Double,
    heightMeasurementStation: Double = 0.0
): Double {
    val pressureAtAltitude = pressureFromAltitude(altitudeInMeters, temperature) // Convert feet to meters and calculate air pressure at given altitude and temperature
    val airPressureAtSeaLevel = altitudeFromPressure(pressureAtAltitude, temperature, pressureAtSeaLevel) // Convert Pa to kPa and calculate air pressure at sea level using calculated air pressure at given altitude and temperature
    return heightMeasurementStation + airPressureAtSeaLevel // Convert kPa to Pa and calculate final altitude using calculated air pressure at sea level and given temperature
}