package fi.infinitygrow.gpslocation.presentation.settings_page.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import fi.infinitygrow.gpslocation.presentation.settings_page.SettingsViewModel
import gpslocation.composeapp.generated.resources.Res
import gpslocation.composeapp.generated.resources.service_running
import gpslocation.composeapp.generated.resources.service_stopped
import gpslocation.composeapp.generated.resources.start_service
import gpslocation.composeapp.generated.resources.stop_service
import org.jetbrains.compose.resources.stringResource

@Composable
fun WeatherServiceControls(
    settingsViewModel: SettingsViewModel
) {
    val isServiceRunning by settingsViewModel.isServiceRunning.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
//        Text(
//            text = "Sää Äänipalvelu",
//            style = MaterialTheme.typography.headlineSmall,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isServiceRunning) stringResource(Res.string.service_running) else stringResource(Res.string.service_stopped),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { settingsViewModel.toggleWeatherService() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isServiceRunning)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isServiceRunning) stringResource(Res.string.stop_service) else stringResource(Res.string.start_service)
                )
            }
        }

        // The button will be visible on all platforms, but only functional on Android
//        if (Platform.isAndroid) {
//            AnimatedVisibility(visible = isServiceRunning) {
//                Text(
//                    text = "Palvelu lukee ääneen säätiedot minuutin välein",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//            }
//        } else {
//            Text(
//                text = "Taustapalvelu on käytettävissä vain Android-laitteissa",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.error,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//        }

//        Button(
//            onClick = { settingsViewModel.speak() },
//        ) {
//            Text(
//                text = "Puhu"
//            )
//        }
    }
}
