package fi.infinitygrow.gpslocation.presentation.utils

import fi.infinitygrow.gpslocation.domain.model.SoundingData
import io.ktor.http.ContentDisposition.Companion.File
import kotlinx.io.files.Path
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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


//@Serializable
data class SoundingPoint(
    val altitude: Double,    // Altitude in meters
    val temperature: Double, // Temperature in °C (environment)
    val dewPoint: Double     // Dew point in °C
)

//
//fun saveSoundingDataToFile(soundingData: List<SoundingPoint>, filePath: String) {
//    val json = Json.encodeToString(soundingData) // Serialize the list to JSON
//    Path(filePath).writeText(json) // Write the JSON string to the file
//}

/**
 * Estimates the LCL altitude using a simple approximation.
 * This uses the formula:
 *    LCL (in meters) ≈ 38.1 * (T - T_d)
 *
 * @param temperature Temperature at the ground level in °C.
 * @param dewPoint Dew point at the ground level in °C.
 * @return Estimated LCL altitude in meters.
 */
fun estimateLCL(temperature: Double, dewPoint: Double): Double =
    (temperature - dewPoint) / 8.0 * 1000.0


//fun estimateLCL(
//    temperature: Double,
//    dewPoint: Double
//): Double = 38.1 * (temperature - dewPoint)


fun isSortedByAltitude(soundingData: List<SoundingData>): Boolean {
    return soundingData.zipWithNext { a, b -> a.altitude <= b.altitude }.all { it }
}


//    println("Before sorting")
//    for (i in 0 .. 10) {
//        println(soundingData[i].altitude)
//    }
//
//    println("After sorting")
//    for (i in 0 .. 10) {
//        println(sortedSoundingData[i].altitude)
//    }

/**
 * Estimates the maximum altitude a parcel (and thus a sailplane) could reach
 * by following the dry and moist adiabatic ascent from the Lifting Condensation Level (LCL)
 * up to the equilibrium level (EL).
 *
 * The function accounts for:
 * - A **dry adiabatic lapse rate** (9.8°C/km) below the LCL.
 * - A **moist adiabatic lapse rate** (6°C/km) above the LCL.
 *
 * It uses sounding data to determine the environmental temperature
 * and assumes the data is sorted by increasing altitude.
 *
 * @param lclAltitude The estimated LCL altitude in meters.
 * @param lclTemperature The temperature at the LCL in °C.
 * @param soundingData The environmental sounding data (must be sorted by altitude).
 * @param dryLapseRate The dry adiabatic lapse rate in °C/m. Default is 0.0098 (9.8°C/km).
 * @param moistLapseRate The moist adiabatic lapse rate in °C/m. Default is 0.006 (6°C/km).
 * @return The estimated maximum altitude (EL) in meters, or null if it cannot be determined.
 */
fun estimateMaxAltitude(
    lclAltitude: Double,
    lclTemperature: Double,
    soundingData: List<SoundingData>,
    dryLapseRate: Double = 0.0098,  // 9.8°C/km
    moistLapseRate: Double = 0.006  // 6°C/km
): Double? {
    val sortedSoundingData = if (isSortedByAltitude(soundingData)) {
        soundingData
    } else {
        soundingData.sortedBy { it.altitude }
    }

    for (i in sortedSoundingData.indices) {
        val point = sortedSoundingData[i]
        if (point.altitude <= lclAltitude) continue

        // Use dry lapse rate before LCL, then switch to moist lapse rate
        val parcelTemperature = if (point.altitude <= lclAltitude) {
            lclTemperature - dryLapseRate * (point.altitude - lclAltitude)
        } else {
            lclTemperature - moistLapseRate * (point.altitude - lclAltitude)
        }

        // Check for equilibrium
        if (parcelTemperature <= point.temperature) {
            if (i > 0) {
                val previousPoint = sortedSoundingData[i - 1]
                val deltaAltitude = point.altitude - previousPoint.altitude
                val deltaTemperature = point.temperature - previousPoint.temperature
                val fraction = (parcelTemperature - previousPoint.temperature) / deltaTemperature
                return previousPoint.altitude + fraction * deltaAltitude
            }
            return point.altitude
        }
    }
    return null
}

/**
 * Estimates the maximum altitude a parcel (and thus a sailplane) could reach
 * by following the dry and moist adiabatic ascent from the ground up to the equilibrium level (EL).
 *
 * The function accounts for:
 * - A **dry adiabatic lapse rate** (9.8°C/km) below the Lifting Condensation Level (LCL).
 * - A **moist adiabatic lapse rate** (6°C/km) above the LCL, if the parcel reaches saturation.
 *
 * It calculates the **starting temperature and dew point at ground level** instead of using a predefined LCL.
 *
 * @param soundingData The environmental sounding data (must be sorted by altitude).
 * @param groundTemperature The observed temperature at ground level (°C).
 * @param groundDewPoint The observed dew point at ground level (°C).
 * @param groundAltitude The starting altitude (meters).
 * @param dryLapseRate The dry adiabatic lapse rate in °C/m. Default is 0.0098 (9.8°C/km).
 * @param moistLapseRate The moist adiabatic lapse rate in °C/m. Default is 0.006 (6°C/km).
 * @return The estimated maximum altitude (EL) in meters, or null if it cannot be determined.
 */
fun estimateMaxAltitudeFromGround(
    soundingData: List<SoundingData>,
    groundTemperature: Double,
    groundDewPoint: Double,
    groundAltitude: Double,
    dryLapseRate: Double = 0.0098,  // 9.8°C/km
    moistLapseRate: Double = 0.006  // 6°C/km
): Double? {
    val sortedSoundingData = if (isSortedByAltitude(soundingData)) {
        soundingData
    } else {
        soundingData.sortedBy { it.altitude }
    }

    // Calculate LCL altitude (cloud base)
    val lclAltitude = groundAltitude + 38.1 * (groundTemperature - groundDewPoint)

    // Calculate temperature at LCL
    val lclTemperature = groundTemperature - dryLapseRate * (lclAltitude - groundAltitude)

    for (i in sortedSoundingData.indices) {
        val point = sortedSoundingData[i]

        // Calculate parcel temperature
        val parcelTemperature = if (point.altitude <= lclAltitude) {
            groundTemperature - dryLapseRate * (point.altitude - groundAltitude)
        } else {
            lclTemperature - moistLapseRate * (point.altitude - lclAltitude)
        }

        // Check for equilibrium (where parcel stops rising)
        if (parcelTemperature <= point.temperature) {
            if (i > 0) {
                val previousPoint = sortedSoundingData[i - 1]
                val deltaAltitude = point.altitude - previousPoint.altitude
                val deltaTemperature = point.temperature - previousPoint.temperature
                val fraction = (parcelTemperature - previousPoint.temperature) / deltaTemperature
                return previousPoint.altitude + fraction * deltaAltitude
            }
            return point.altitude
        }
    }
    return null
}



fun estimateMaxAltitudeNoLCL(
    soundingData: List<SoundingData>,
    startingTemperature: Double, // Temperature at the starting altitude
    startAltitude: Double,
    dryLapseRate: Double = 0.0098  // 9.8°C/km
): Double? {
    val sortedSoundingData = if (isSortedByAltitude(soundingData)) {
        soundingData
    } else {
        soundingData.sortedBy { it.altitude }
    }

    for (i in sortedSoundingData.indices) {
        val point = sortedSoundingData[i]
        // Calculate parcel temperature using dry lapse rate only
        val parcelTemperature = startingTemperature - dryLapseRate * (point.altitude - startAltitude)

        // Check for equilibrium
        if (parcelTemperature <= point.temperature) {
            if (i > 0) {
                val previousPoint = sortedSoundingData[i - 1]
                val deltaAltitude = point.altitude - previousPoint.altitude
                val deltaTemperature = point.temperature - previousPoint.temperature
                val fraction = (parcelTemperature - previousPoint.temperature) / deltaTemperature
                return previousPoint.altitude + fraction * deltaAltitude
            }
            return point.altitude
        }
    }
    return null
}


//// Example usage:
//fun example() {
//    // Ground measurements (in °C)
//    val groundTemp = 20.0
//    val groundDewPoint = 12.0
//
//    // Estimate LCL altitude (cloud base)
//    val lclAltitude = estimateLCL(groundTemp, groundDewPoint)
//    println("Estimated LCL altitude: $lclAltitude m")
//
//    // Assume temperature at LCL follows a simple rule (or interpolate from sounding data)
//    val lclTemp = groundTemp - 9.8 * (lclAltitude / 1000.0)
//    println("Estimated LCL temperature: $lclTemp °C")
//
//    // Example sounding data: list of points in the environment
//    val sounding = listOf(
//        S(altitude = 0.0, temperature = 20.0, dewPoint = 12.0),
//        SoundingPoint(altitude = 500.0, temperature = 16.0, dewPoint = 12.5),
//        SoundingPoint(altitude = 1000.0, temperature = 12.0, dewPoint = 12.0),
//        SoundingPoint(altitude = 1500.0, temperature = 8.0, dewPoint = 10.0),
//        SoundingPoint(altitude = 2000.0, temperature = 4.0, dewPoint = 8.0)
//    )
//
//    // Estimate maximum altitude using the moist adiabatic ascent from the LCL
//    val maxAltitude = estimateMaxAltitude(lclAltitude, lclTemp, sounding)
//    if (maxAltitude != null) {
//        println("Estimated maximum altitude (EL): $maxAltitude m")
//    } else {
//        println("Could not determine an equilibrium level from the provided sounding.")
//    }
//}