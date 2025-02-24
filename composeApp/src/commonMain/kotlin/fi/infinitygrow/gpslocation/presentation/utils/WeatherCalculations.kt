package fi.infinitygrow.gpslocation.presentation.utils

import kotlin.math.exp
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

    return pressureAtSeaLevel * exp((-g0 * M * altitudeDiff) / (R * temperatureAtAltitude))
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

/**
 * Calculates altitude from given temperature and altitude in feet and then uses the result air pressure with `altitudeFromPressure()` with same temperature and given pressure at sea level to calculate final altitude in meters.
 *
 * @param temperature The temperature in Kelvin (K).
 * @param altitudeInFeet The altitude in feet.
 * @param pressureAtSeaLevel The air pressure at sea level in Pascals (Pa).
 * @return The final altitude in meters (m).
 */
fun calculateAltitude(
    temperature: Double,
    altitudeInFeet: Double,
    pressureAtSeaLevel: Double,
    heightMeasurementStation: Double = 0.0
): Double {
    val pressureAtAltitude = pressureFromAltitude(altitudeInFeet, temperature) // Convert feet to meters and calculate air pressure at given altitude and temperature
    val airPressureAtSeaLevel = altitudeFromPressure(pressureAtAltitude, temperature, pressureAtSeaLevel) // Convert Pa to kPa and calculate air pressure at sea level using calculated air pressure at given altitude and temperature
    return heightMeasurementStation + airPressureAtSeaLevel // Convert kPa to Pa and calculate final altitude using calculated air pressure at sea level and given temperature
}