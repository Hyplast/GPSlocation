package fi.infinitygrow.gpslocation.presentation.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual class LocationService(
    private val context: Context//,
    //private val launcher: ActivityResultLauncher<String>
) {

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    actual fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @Suppress("MissingPermission")
    actual suspend fun getLocation(): Location? = suspendCoroutine { continuation ->
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location ->
            location?.let {
                continuation.resumeWith(
                    Result.success(
                        Location(
                            location.latitude,
                            location.longitude
                        )
                    )
                )
            }

        }?.addOnFailureListener {
            continuation.resumeWithException(Exception("Unable to get location."))
        }
    }

    actual fun requestLocationPermission(granted: (Boolean) -> Unit) {
        // This must be handled in your Android Activity or Compose permission launcher
        granted(isPermissionGranted())
        //launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}