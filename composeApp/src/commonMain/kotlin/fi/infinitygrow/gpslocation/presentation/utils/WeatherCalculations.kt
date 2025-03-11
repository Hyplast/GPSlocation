package fi.infinitygrow.gpslocation.presentation.utils

import kotlin.math.exp
import kotlin.math.log
import kotlin.math.pow

/**
 * Calculates the approximate height of the cloud base above ground level.
 *
 * This function uses the temperature-dew point spread method to estimate the cloud base height.
 * It assumes a standard atmospheric lapse rate and a constant dew point depression.
 *
 * @param temperatureC The air temperature at the station in degrees Celsius (°C).
 * @param dewPointC The dew point temperature at the station in degrees Celsius (°C).
 * @param heightStationM The height of the weather station above sea level in meters (m).
 * @return The estimated height of the cloud base above ground level in meters (m).
 */
fun calculateCloudBaseHeight(temperatureC: Double, dewPointC: Double, heightStationM: Double): Double {
    return ((temperatureC - dewPointC) / 10) * 1247 + heightStationM
}

private const val Tb = 288.15 // Standard temperature at sea level in Kelvin
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

    return pressureAtSeaLevel * exp((-g0 * M * altitude) / (R * temperatureAtAltitude))//temperatureAtAltitude))
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

/**
 * Calculates the altitude based on atmospheric pressure, sea-level pressure, and sea-level temperature.
 *
 * This function uses the standard atmospheric model to estimate the altitude at a specific pressure.
 * It handles two different atmospheric layers: the troposphere (up to 11,000 meters) and the lower
 * stratosphere (between 11,000 and 20,000 meters).
 *
 * @param p The sea-level pressure in Pascals (Pa). Defaults to [pDefault].
 * @param t The sea-level temperature in Kelvin (K). Defaults to [tDefault].
 * @param pa The atmospheric pressure at the target altitude in Pascals (Pa).
 * @return The estimated altitude in meters (m), or [Double.NaN] if the pressure ratio is outside the
 *         supported range.
 *
 * @see pDefault
 * @see tDefault
 */
fun altcalc(
    p: Double = pDefault,
    t: Double = tDefault,
    pa: Double
): Double {
    return when {
        (p / pa) < (pDefault / 22632.1) -> {
            val lapseRate = -0.0065
            val intercept = 0.0
            // Here, note that a coefficient is computed using p and pa from
            // parameters rather than the hard-coded pDefault.
            val coeff = (pa / p).pow((R * lapseRate) / (g0 * M))
            intercept + (t * ((1 / coeff) - 1) / lapseRate)
        }
        (p / pa) < (pDefault / 5474.89) -> {
            val hBase = 11000.0
            val tempAdjustment = t - 71.5
            // Using logarithm with two arguments, note that log(x, base) here means
            // logarithm of x with base given by the second argument.
            val deltaTemp = (R * tempAdjustment * log(pa, p)) / (-g0 * M)
            val basePressure = pDefault
            val constantPressure = 22632.1
            val baseDelta = (R * tempAdjustment * log(basePressure, constantPressure)) /
                    (-g0 * M) + hBase
            baseDelta + deltaTemp
        }
        else -> Double.NaN
    }
}

/**
 * Calculates the atmospheric pressure at a given altitude based on sea-level pressure and temperature.
 *
 * This function uses the standard atmospheric model to estimate the pressure at a specific altitude.
 * It handles two different atmospheric layers: the troposphere (up to 11,000 meters) and the lower
 * stratosphere (between 11,000 and 20,000 meters).
 *
 * @param p The sea-level pressure in Pascals (Pa). Defaults to [pDefault].
 * @param t The sea-level temperature in Kelvin (K). Defaults to [tDefault].
 * @param h The altitude in meters (m).
 * @return The estimated atmospheric pressure at the given altitude in Pascals (Pa), or [Double.NaN]
 *         if the altitude is outside the supported range (above 20,000 meters).
 *
 * @see pDefault
 * @see tDefault
 */
fun pressTempAlt(
    p: Double = pDefault,
    t: Double = tDefault,
    h: Double
): Double {
    return when {
        h < 11000 -> {
            val lapseRate = -0.0065
            val intercept = 0.0
            p * (t / (t + (lapseRate * (h - intercept)))).pow((g0 * M) / (R * lapseRate))
        }
        h <= 20000 -> {
            val lapseRate = -0.0065
            val intercept = 0.0
            val hBase = 11000.0
            val paBase = p * (t / (t + (lapseRate * (hBase - intercept)))).pow(
                (g0 * M) / (R * lapseRate)
            )
            val tBase = t + (lapseRate * hBase)
            paBase * exp((-g0 * M * (h - hBase)) / (R * tBase))
        }
        else -> Double.NaN
    }
}

/**
 * Calculates the altitude based on sea-level pressure, sea-level temperature, and a target altitude.
 *
 * This function combines the functionality of [pressTempAlt] and [altcalc] to perform a chained
 * calculation. It first estimates the atmospheric pressure at the target altitude using
 * [pressTempAlt], and then uses that pressure to estimate the altitude using [altcalc].
 *
 * @param p The sea-level pressure in Pascals (Pa). Defaults to [pDefault].
 * @param t The sea-level temperature in Kelvin (K). Defaults to [tDefault].
 * @param altitude The target altitude in meters (m).
 * @return The estimated altitude in meters (m).
 *
 * @see pressTempAlt
 * @see altcalc
 * @see pDefault
 * @see tDefault
 */
fun pressureTemperatureAltitude(
    p: Double = pDefault,
    t: Double = tDefault,
    altitude: Double
): Double {
    val pa = pressTempAlt(h = altitude)
    return altcalc(p = p,t = t, pa = pa)
}

fun pressureTemperatureAltitudeWHeight(
    p: Double = pDefault,
    t: Double = tDefault,
    altitude: Double,
    stationHeight: Double
): Double {
    val pa = pressTempAlt(h = altitude)
    val seaLevelTemp = calcSeaLevelTemperature(t, stationHeight)
    return altcalc(p = p,t = seaLevelTemp, pa = pa)
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
    val pressureAtAltitude = pressureFromAltitude(altitude = altitudeInMeters, temperatureAtStation = temperature, heightStationM = heightMeasurementStation) // Convert feet to meters and calculate air pressure at given altitude and temperature
    val altitudeAtPressure = altitudeFromPressure(pressureAtAltitude, temperature, pressureAtSeaLevel) // Convert Pa to kPa and calculate air pressure at sea level using calculated air pressure at given altitude and temperature
    return altitudeAtPressure // Convert kPa to Pa and calculate final altitude using calculated air pressure at sea level and given temperature
}