package fi.infinitygrow.gpslocation.presentation.settings_page.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


@Composable
fun PreferencesSection(
    isDarkTheme: Boolean,
    isLocationOn: Boolean,
    onDarkThemeToggle: () -> Unit,
    onLocationToggle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (isDarkTheme) "Dark Theme: ON" else "Dark Theme: OFF",
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            Switch(
                modifier = Modifier.semantics { contentDescription = "Dark Theme" },
                checked = isDarkTheme,
                onCheckedChange = { onDarkThemeToggle() }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (isLocationOn) "Paikannus: Päällä" else "Paikannus: Pois",
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            Switch(
                modifier = Modifier.semantics { contentDescription = "Paikannus" },
                checked = isLocationOn,
                onCheckedChange = { onLocationToggle() }
            )
        }
    }
}