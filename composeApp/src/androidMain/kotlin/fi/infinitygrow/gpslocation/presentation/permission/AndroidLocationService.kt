package fi.infinitygrow.gpslocation.presentation.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.github.aakira.napier.Napier
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import android.location.Location as AndroidLocation // Alias to avoid confusion
import android.Manifest

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

actual class LocationService(
    private val context: Context,
) {

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    actual fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @Suppress("MissingPermission")
    actual suspend fun getLocation(): Location? = suspendCoroutine { continuation ->
        Napier.d("Getting location...", tag = "LocationService")
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: AndroidLocation? ->  // Explicit type
                if (location != null) {
                    Napier.d("Location received: ${location.latitude}, ${location.longitude}", tag = "LocationService")
                    continuation.resume(
                        Location(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                } else {
                    Napier.w("Last known location is null", tag = "LocationService") // Warning instead of error
                    continuation.resume(null) // Resume with null location
                }
            }
            .addOnFailureListener { exception: Exception -> // Explicit type
                Napier.e("Location retrieval failed: ${exception.message}", tag = "LocationService")
                continuation.resumeWithException(LocationException("Unable to get location: ${exception.message}")) // Custom Exception
            }
    }

    actual fun requestLocationPermission(granted: (Boolean) -> Unit) {
        // This must be handled in your Android Activity or Compose permission launcher
        granted(isPermissionGranted())
    }
}

// Define a custom exception for location failures
class LocationException(message: String) : Exception(message)

/*
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
        Napier.d("Getting location...", tag = "Timber")
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location ->
            location?.let {
                Napier.d("Location received + ${location.latitude} ${location.longitude}", tag = "Timber")
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
            Napier.e("Location failed + ${it.message}", tag = "Timber")
            continuation.resumeWithException(Exception("Unable to get location."))
        }
    }

    actual fun requestLocationPermission(granted: (Boolean) -> Unit) {
        // This must be handled in your Android Activity or Compose permission launcher
        granted(isPermissionGranted())
        //launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

 */