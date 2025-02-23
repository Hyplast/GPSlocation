package fi.infinitygrow.gpslocation.presentation.utils

import kotlin.math.pow

fun calculateCloudBaseHeight(temperatureC: Double, dewPointC: Double, heightStationM: Double): Double {
    return ((temperatureC - dewPointC) / 10) * 1247 + heightStationM
}

private const val Tb = 288 // Standard temperature at sea level in Kelvin
private const val Lb = -0.0065 // Standard temperature lapse rate in Kelvin per meter
private const val R = 8.31432 // Universal gas constant in J/(mol*K)
private const val g0 = 9.80665 // Standard gravitational acceleration in m/s^2
private const val M = 0.028964 // Molar mass of dry air in kg/mol

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
 * Calculates air pressure from altitude and temperature.
 *
 * @param altitude The altitude in meters (m).
 * @param temperature The temperature in Kelvin (K).
 * @param pressureAtSeaLevel The air pressure at sea level in Pascals (Pa).
 * @param heightStationM Height of the observation in meters (m).
 * @return The air pressure in Pascals (Pa).
 */
fun pressureFromAltitude(altitude: Double, temperature: Double, pressureAtSeaLevel: Double = 101325.0, heightStationM: Double = 0.0): Double {
    return pressureAtSeaLevel * (1 - 0.0065/temperature * (altitude - 0)).pow((-9.80665*0.0289644)/(8.31432*(-0.0065)))
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