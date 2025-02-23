package fi.infinitygrow.gpslocation.presentation.permission

//expect fun LocationServiceFactory(): LocationService


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class LocationService {

    suspend fun getLocation(): Location?

    fun isPermissionGranted(): Boolean

    fun requestLocationPermission(granted: (Boolean) -> Unit)

}

data class Location(
    val latitude: Double,
    val longitude: Double
)