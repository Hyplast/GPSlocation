package fi.infinitygrow.gpslocation.domain.model

import fi.infinitygrow.gpslocation.presentation.permission.Location

data class RadiationData(
    val name: String,
    val coordinates: Location,
    val longitude: Double,
    val latitude: Double,
    val unixTime: Long,
    val longWaveIn: Double,         // LWIN_1MIN - Long wave solar radiation - UV radiation - W/m2
    val longWaveOut: Double,        // LWOUT_1MIN - Long wave outgoing solar radiation - UV radiation - W/m2
    val globalRadiation: Double,    // GLOB_1MIN - Global radiation - Solar radiation - W/m2
    val directRadiation: Double,    // DIR_1MIN - Direct solar radiation - Solar radiation - W/m2
    val reflectedRadiation: Double, // REFL_1MIN - Reflected radiation - Solar radiation - W/m2
    val sunshineDuration: Double,   // SUND_1MIN - Sunshine duration - Sunshine duration - s
    val diffuseRadiation: Double,   // DIFF_1MIN - Diffuse radiation - Solar radiation - W/m2
    val radiationBalance: Double,   // NET_1MIN - Radiation balance - Solar radiation - W/m2
    val uvRadiation: Double,        // NET_1MIN - Ultraviolet irradiance - UV radiation - index
)


sealed class DataItem {
    abstract val name: String
    abstract val coordinates: Location
    abstract val longitude: Double
    abstract val latitude: Double
    abstract val unixTime: Long

    data class RadiationItem(
        override val name: String,
        override val coordinates: Location,
        override val longitude: Double,
        override val latitude: Double,
        override val unixTime: Long,
        val longWaveIn: Double,         // LWIN_1MIN
        val longWaveOut: Double,        // LWOUT_1MIN
        val globalRadiation: Double,    // GLOB_1MIN
        val directRadiation: Double,    // DIR_1MIN
        val reflectedRadiation: Double, // REFL_1MIN
        val sunshineDuration: Double,   // SUND_1MIN
        val diffuseRadiation: Double,   // DIFF_1MIN
        val radiationBalance: Double,   // NET_1MIN
        val uvRadiation: Double         // UVRADIATION
    ) : DataItem()

    data class ObservationItem(
        override val name: String,
        override val coordinates: Location,
        override val longitude: Double,
        override val latitude: Double,
        override val unixTime: Long,
        val temperature: Double,
        val windSpeed: Double,
        val windGust: Double,
        val windDirection: Double,
        val humidity: Double,
        val dewPoint: Double,
        val precipitationAmount: Double,
        val precipitationIntensity: Double,
        val snowDepth: Double,
        val pressure: Double,
        val visibility: Double,
        val cloudAmount: Double,
        val presentWeather: Double
    ) : DataItem()
}

fun mapToDataItem(radiationData: RadiationData? = null,
                  observationData: ObservationData? = null): DataItem? {
    return when {
        radiationData != null && observationData == null -> {
            DataItem.RadiationItem(
                name = radiationData.name,
                coordinates = radiationData.coordinates,
                longitude = radiationData.longitude,
                latitude = radiationData.latitude,
                unixTime = radiationData.unixTime,
                longWaveIn = radiationData.longWaveIn,
                longWaveOut = radiationData.longWaveOut,
                globalRadiation = radiationData.globalRadiation,
                directRadiation = radiationData.directRadiation,
                reflectedRadiation = radiationData.reflectedRadiation,
                sunshineDuration = radiationData.sunshineDuration,
                diffuseRadiation = radiationData.diffuseRadiation,
                radiationBalance = radiationData.radiationBalance,
                uvRadiation = radiationData.uvRadiation
            )
        }
        observationData != null && radiationData == null -> {
            DataItem.ObservationItem(
                name = observationData.name,
                coordinates = observationData.coordinates,
                longitude = observationData.longitude,
                latitude = observationData.latitude,
                unixTime = observationData.unixTime,
                temperature = observationData.temperature,
                windSpeed = observationData.windSpeed,
                windGust = observationData.windGust,
                windDirection = observationData.windDirection,
                humidity = observationData.humidity,
                dewPoint = observationData.dewPoint,
                precipitationAmount = observationData.precipitationAmount,
                precipitationIntensity = observationData.precipitationIntensity,
                snowDepth = observationData.snowDepth,
                pressure = observationData.pressure,
                visibility = observationData.visibility,
                cloudAmount = observationData.cloudAmount,
                presentWeather = observationData.presentWeather
            )
        }
        else -> null // If you want to handle combined cases differently,
        // you can extend the model accordingly.
    }
}

