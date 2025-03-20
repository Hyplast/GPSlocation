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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DropdownMenuItem
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
import gpslocation.composeapp.generated.resources.Res
import gpslocation.composeapp.generated.resources.back
import gpslocation.composeapp.generated.resources.clouds_height
import gpslocation.composeapp.generated.resources.dark_theme
import gpslocation.composeapp.generated.resources.direction_plain
import gpslocation.composeapp.generated.resources.distance
import gpslocation.composeapp.generated.resources.gust
import gpslocation.composeapp.generated.resources.humidity
import gpslocation.composeapp.generated.resources.location
import gpslocation.composeapp.generated.resources.location_denied
import gpslocation.composeapp.generated.resources.one_or_all_all
import gpslocation.composeapp.generated.resources.road_observations
import gpslocation.composeapp.generated.resources.settings
import gpslocation.composeapp.generated.resources.start
import gpslocation.composeapp.generated.resources.station_name
import gpslocation.composeapp.generated.resources.talk_service
import gpslocation.composeapp.generated.resources.temperature
import gpslocation.composeapp.generated.resources.wind
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    // Collect settings state from the view model.
    val darkTheme by settingsViewModel.darkTheme.collectAsState()
    val isLocationOn by settingsViewModel.isLocationOn.collectAsState()
    val currentRadius by settingsViewModel.radius.collectAsState() // defaults to 50
    val isServiceRunning by settingsViewModel.isServiceRunning.collectAsState()
    val textToSpeech by settingsViewModel.textToSpeech.collectAsState()
    val ttsName by settingsViewModel.ttsName.collectAsState()
    val ttsDistance by settingsViewModel.ttsDistance.collectAsState()
    val ttsOneOrAll by settingsViewModel.ttsOneOrAll.collectAsState()
    val ttsRoadObservations by settingsViewModel.ttsRoadObservations.collectAsState()
    val ttsTemperature by settingsViewModel.ttsTemperature.collectAsState()
    val ttsHumidity by settingsViewModel.ttsHumidity.collectAsState()
    val ttsWindSpeed by settingsViewModel.ttsWindSpeed.collectAsState()
    val ttsWindGust by settingsViewModel.ttsWindGust.collectAsState()
    val ttsWindDirection by settingsViewModel.ttsWindDirection.collectAsState()
    val ttsCloudBase by settingsViewModel.ttsCloudBase.collectAsState()

    val ttsFlightLevel65 by settingsViewModel.ttsFlightLevel65.collectAsState()
    val ttsFlightLevel95 by settingsViewModel.ttsFlightLevel95.collectAsState()

    val radiusOptions = listOf(15, 30, 50)
    var dropdownExpanded by remember { mutableStateOf(false) }

    //var talkService by remember { mutableStateOf(false) }
    val talkServiceSwitch by settingsViewModel.talkServiceSwitch.collectAsState()

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val backgroundColor = if (darkTheme) Color.Black else Color.White
    val fontColor = if (darkTheme) Color.White else Color.Black

    var isLocationOnSwitch = isLocationOn

    var selected by remember { mutableStateOf(false) }
    var showChips by remember { mutableStateOf(false) }

    val locationDeniedText = stringResource(Res.string.location_denied)

    val chipLabels =
        listOf(
            stringResource(Res.string.one_or_all_all),
            stringResource(Res.string.road_observations),
            stringResource(Res.string.station_name),
            stringResource(Res.string.distance),
            stringResource(Res.string.temperature),
            stringResource(Res.string.humidity),
            stringResource(Res.string.wind),
            stringResource(Res.string.gust),
            stringResource(Res.string.direction_plain),
            stringResource(Res.string.clouds_height),
            "FL65",
            "FL95"
        )

    val chipSelections = listOf(
        ttsOneOrAll,
        ttsRoadObservations,
        ttsName,
        ttsDistance,
        ttsTemperature,
        ttsHumidity,
        ttsWindSpeed,
        ttsWindGust,
        ttsWindDirection,
        ttsCloudBase,
        ttsFlightLevel65,
        ttsFlightLevel95
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
                title = { stringResource(Res.string.settings) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
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
                    text = stringResource(Res.string.dark_theme),
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
                    text = stringResource(Res.string.location),
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
                                    message = locationDeniedText,
                                )
                            }
                        }
                    }
                )

            }
            if (isLocationOnSwitch) {
                Spacer(modifier = Modifier.height(16.dp))
                // A Box is used to anchor the DropdownMenu.
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Button showing the current selection; clicking it expands the dropdown.
                    OutlinedButton(
                        onClick = { dropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Havaintoasemien hakuetäisyys $currentRadius km")
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        radiusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = "$option km") },
                                onClick = {
                                    scope.launch {
                                        settingsViewModel.setRadius(option)
                                    }
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.talk_service),
                    color = fontColor
                )
                Switch(
                    checked = talkServiceSwitch,
                    onCheckedChange = { checked ->
                        settingsViewModel.setTalkService(checked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (talkServiceSwitch) {
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
                        chipLabels.forEachIndexed { index, label ->
                            FilterChip(
                                selected = chipSelections[index],
                                onClick = {
                                    // Toggle corresponding TTS variable via the view model.
                                    when (index) {
                                        0 -> settingsViewModel.toggleTtsOneOrAll()
                                        1 -> settingsViewModel.toggleTtsRoadObservations()
                                        2 -> settingsViewModel.toggleTtsName()
                                        3 -> settingsViewModel.toggleTtsDistance()
                                        4 -> settingsViewModel.toggleTtsTemperature()
                                        5 -> settingsViewModel.toggleTtsHumidity()
                                        6 -> settingsViewModel.toggleTtsWindSpeed()
                                        7 -> settingsViewModel.toggleTtsWindGust()
                                        8 -> settingsViewModel.toggleTtsWindDirection()
                                        9 -> settingsViewModel.toggleTtsCloudBase()
                                        10 -> settingsViewModel.toggleTtsFlightLevel65()
                                        11 -> settingsViewModel.toggleTtsFlightLevel95()
                                    }
                                },
                                label = { Text(label) },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
//                            AssistChip(
//                                modifier =
//                                Modifier.padding(horizontal = 4.dp),
//                                onClick = { /* do something*/ },
//                                label = { Text(element) }
//                            )
                        }
                    }

//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//
//                    Button(
//                        onClick = {
//                            //showChips = false
//                            settingsViewModel.toggleTalkService()
//                        }, // Trigger the lambda when the button is clicked
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        Text(stringResource(Res.string.start))
//                    }
//                }
                Spacer(modifier = Modifier.height(16.dp))
                WeatherServiceControls(settingsViewModel)
            }
        }
    }
}



