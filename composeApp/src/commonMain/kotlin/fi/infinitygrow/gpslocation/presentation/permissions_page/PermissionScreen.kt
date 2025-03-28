package fi.infinitygrow.gpslocation.presentation.permissions_page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.permissions.PermissionState

@Composable
fun PermissionsScreen(
    viewModel: PermissionsViewModel,
    onPermissionGranted: () -> Unit
) {
    val state = viewModel.state

    // Navigate away once permission is granted
    LaunchedEffect(state) {
        if (state == PermissionState.Granted) {
            onPermissionGranted()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Location Permission Required")
        Spacer(Modifier.height(8.dp))
        Text(
            "We need location access to show you the weather for your current area.",
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        when (state) {
            PermissionState.DeniedAlways -> {
                Text("Permission permanently denied. Please enable in settings.")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.openAppSettings() }) {
                    Text("Open App Settings")
                }
            }
            PermissionState.Granted -> {
                // Usually navigates away before showing this, but good fallback
                Text("Permission Granted! Loading weather...")
                CircularProgressIndicator()
            }
            else -> { // NotDetermined or Denied
                Button(onClick = { viewModel.provideOrRequestLocationPermission() }) {
                    Text("Grant Location Permission")
                }
            }
        }
    }
}