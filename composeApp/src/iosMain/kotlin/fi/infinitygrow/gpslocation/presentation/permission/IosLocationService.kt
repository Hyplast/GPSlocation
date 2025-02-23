package fi.infinitygrow.gpslocation.presentation.permission

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.darwin.NSObject

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class LocationService  {

    private val locationManager = CLLocationManager()

    actual fun isPermissionGranted(): Boolean {
        val status = locationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedAlways || status == kCLAuthorizationStatusAuthorizedWhenInUse
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getLocation(): Location? {
        return locationManager.location?.coordinate?.useContents {
            Location(latitude, longitude)
        }
    }

    actual fun requestLocationPermission(granted: (Boolean) -> Unit) {
        val status = locationManager.authorizationStatus()
        if (status == kCLAuthorizationStatusNotDetermined) {
            locationManager.delegate = LocationDelegate(granted)
            locationManager.requestWhenInUseAuthorization()
        }else{
            granted(true)
        }
    }
}

private class LocationDelegate(val granted: (Boolean) -> Unit) : NSObject(),
    CLLocationManagerDelegateProtocol {

    override fun locationManager(
        manager: CLLocationManager,
        didChangeAuthorizationStatus: CLAuthorizationStatus
    ) {
        when (didChangeAuthorizationStatus) {
            kCLAuthorizationStatusAuthorizedAlways, kCLAuthorizationStatusAuthorizedWhenInUse -> {
                granted.invoke(true)
            }

            else -> {
                granted.invoke(false)
            }
        }
    }

}