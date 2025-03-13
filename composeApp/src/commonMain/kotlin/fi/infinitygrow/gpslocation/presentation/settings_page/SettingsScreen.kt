package fi.infinitygrow.gpslocation.presentation.settings_page

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import fi.infinitygrow.gpslocation.presentation.settings_page.components.WeatherServiceControls
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    // Collect settings state from the view model.
    val darkTheme by settingsViewModel.darkTheme.collectAsState()
    val isLocationOn by settingsViewModel.isLocationOn.collectAsState()
    var talkService by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val backgroundColor = if (darkTheme) Color.Black else Color.White
    val fontColor = if (darkTheme) Color.White else Color.Black

    var isLocationOnSwitch = isLocationOn

    var selected by remember { mutableStateOf(false) }
    var showChips by remember { mutableStateOf(false) }

    val colorNames =
        listOf(
            "Etäisyys",
            "Lämpötila",
            "Kosteus",
            "Tuuli",
            "Puuska",
            "Suunta",
            "Pilvikorkeus",
            "Lentopinnat"
        )


    LaunchedEffect(Unit) {
        isLocationOnSwitch = if (isLocationOn && !settingsViewModel.isPermissionGranted) {
            false
        } else {
            isLocationOn
        }

    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(16.dp)

        ) {
            // Toggle dark theme setting.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dark Theme",
                    color = fontColor
                )
                Switch(
                    checked = darkTheme,
                    onCheckedChange = { checked ->
                        scope.launch {
                            settingsViewModel.toggleDarkTheme()
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Toggle location setting.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Location",
                    color = fontColor
                )
                Switch(
                    checked = isLocationOnSwitch,
                    onCheckedChange = { checked ->
                        scope.launch {
                            val success = settingsViewModel.toggleLocation()
                            if (!success) {
                                // Show snackbar if permission is denied
                                snackbarHostState.showSnackbar(
                                    message = "Location permission denied. Please enable in settings."
                                )
                            }
                        }
                    }
                )

            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Puhu havainnot",
                    color = fontColor
                )
                Switch(
                    checked = talkService,
                    onCheckedChange = { checked ->
                        showChips = checked
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (showChips) {
                Column {
                    FlowRow(
                        modifier =
                        Modifier.fillMaxWidth(1f)
                            .wrapContentHeight(align = Alignment.Top)
                            .then(
                                if (selected) {
                                    Modifier.verticalScroll(rememberScrollState())
                                } else {
                                    Modifier.horizontalScroll(rememberScrollState())
                                }
                            ),
                        horizontalArrangement = Arrangement.Start,
                        maxLines = if (!selected) 1 else Int.MAX_VALUE,
                    ) {
                        Box(
                            Modifier.height(FilterChipDefaults.Height),
                        ) {
                            VerticalDivider()
                        }
                        colorNames.fastForEachIndexed { index, element ->
                            AssistChip(
                                modifier =
                                Modifier.padding(horizontal = 4.dp),
                                onClick = { /* do something*/ },
                                label = { Text(element) }
                            )
                        }
                    }

                    Column(modifier = Modifier
                        .fillMaxWidth(),
                         horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                    Button(
                        onClick = {
                            //showChips = false
                            settingsViewModel.toggleTalkService()
                        }, // Trigger the lambda when the button is clicked
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Käynnistä")
                    }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            WeatherServiceControls(settingsViewModel)
        }
    }
}


