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


