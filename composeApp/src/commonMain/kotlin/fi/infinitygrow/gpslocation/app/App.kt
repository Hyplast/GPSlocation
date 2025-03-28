package fi.infinitygrow.gpslocation.app


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import fi.infinitygrow.gpslocation.presentation.observation_list.WeatherScreen
import fi.infinitygrow.gpslocation.presentation.observation_list.WeatherViewModel
import fi.infinitygrow.gpslocation.presentation.permissions_page.PermissionsScreen
import fi.infinitygrow.gpslocation.presentation.permissions_page.PermissionsViewModel
import fi.infinitygrow.gpslocation.presentation.settings_page.SettingsScreen
import fi.infinitygrow.gpslocation.presentation.settings_page.SettingsViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf // Needed for passing parameters

/*
TODO LIST ()
**!1. ForegroundService implementation ----DONE
    a) Timer update-                     ---DONE
    b) iOs implementation
**!2. TTS service implementation
    a) Chips implement                 ----DONE
3. Lightning warning
**!4. Favorites implementation -> Save any station as favorite  ----DONE
5. Sun Radiation -> implement on same card (with sealed classes ?) ----DONE
6. Better graph library implementation                           ----DONE
!7. Test that the app works without location
!8. Swipe to remove from screen LazyGrid?
9. Rain Intensity graph add
10. Sounding implementation                                         ----DONE
    a) Plot to graph                                               ----DONE
    b) Get thermal height, upper wind strength, dust devil parameters?, FL true heights
11. Proper navigation implementation
12. Dark theme implementation
!13. Moko permissions
!14. Translate resources xml in everywhere
 */
@Composable
@Preview
fun App() {
    MaterialTheme {
        val factory = rememberPermissionsControllerFactory()
        val controller = remember(factory) {
            factory.createPermissionsController()
        }
        BindEffect(controller)

        val permissionsViewModel: PermissionsViewModel = koinViewModel(
            parameters = { parametersOf(controller) } // Pass controller instance here
        )
        val initialPermissionState = permissionsViewModel.state

        var startDestination by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) { // Check once on launch
            val currentState = controller.getPermissionState(Permission.LOCATION)
            startDestination = if (currentState == PermissionState.Granted) {
                "weather"
            } else {
                "permissions"
            }
        }

        if (startDestination != null) { // Wait until check is complete
            WeatherAppNav(
                startDestination = startDestination!!, // Use determined start destination
                permissionsController = controller // Pass controller for param resolution
            )
        } else {
            // Optional: Show a loading indicator while checking permission
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun WeatherAppNav(
    startDestination: String,
    permissionsController: PermissionsController // Pass controller down
) {
    val navController = rememberNavController()

    val weatherViewModel = koinViewModel<WeatherViewModel>()
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val permissionsViewModel: PermissionsViewModel = koinViewModel(
        parameters = { parametersOf(permissionsController) }
    )

    //val startDestination = "permissions"

    NavHost(
        navController = navController,
        startDestination = startDestination // Use dynamic start destination
    ) {
        composable("permissions") {
            // Resolve ViewModel here, passing controller
            PermissionsScreen(
                viewModel = permissionsViewModel,
                onPermissionGranted = {
                    // Navigate to weather and clear permissions screen from backstack
                    navController.navigate("weather") {
                        popUpTo("permissions") { inclusive = true }
                    }
                }
            )
        }
        composable("weather") {
            WeatherScreen(
                weatherViewModel = weatherViewModel,
                // Potentially pass controller or a re-check function if needed
                onNavigateToSettings = { navController.navigate("settings") },
                permissionsViewModel = permissionsViewModel
            )
        }
        composable("settings") {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                //permissionsViewModel = permissionsViewModel, // Pass it here
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}

/*
@Composable
fun WeatherAppNav2(
    weatherViewModel: WeatherViewModel,
    settingsViewModel: SettingsViewModel,
    permissionsViewModel: PermissionsViewModel,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "weather"
    ) {
        composable("weather") {
            WeatherScreen(
                weatherViewModel = weatherViewModel,
                permissionsViewModel = permissionsViewModel, // Pass it here
                //locationService = locationService,
                //prefs = prefs,
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                //permissionsViewModel = permissionsViewModel, // Pass it here
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}


 */

/*
// MaterialTheme (
val factory = rememberPermissionsControllerFactory()
val controller = remember(factory) {
    factory.createPermissionsController()
}

BindEffect(controller)

val viewModel = viewModel {
    PermissionsViewModel(controller)
}

Column(
    modifier = Modifier
        .fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    when(viewModel.state) {
        PermissionState.Granted -> {
            Text("Location permission granted!")
        }
        PermissionState.DeniedAlways -> {
            Text("Permission was permanently declined.")
            Button(onClick = {
                controller.openAppSettings()
            }) {
                Text("Open app settings")
            }
        }
        else -> {
            Button(
                onClick = {
                    viewModel.provideOrRequestRecordAudioPermission()
                }
            ) {
                Text("Request permission")
            }
        }
    }
}
 */

