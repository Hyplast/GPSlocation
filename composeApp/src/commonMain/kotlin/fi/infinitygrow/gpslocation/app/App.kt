package fi.infinitygrow.gpslocation.app


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fi.infinitygrow.gpslocation.presentation.observation_list.WeatherScreen
import fi.infinitygrow.gpslocation.presentation.observation_list.WeatherViewModel
import fi.infinitygrow.gpslocation.presentation.settings_page.SettingsScreen
import fi.infinitygrow.gpslocation.presentation.settings_page.SettingsViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

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
        val viewModel = koinViewModel<WeatherViewModel>()
        val settingsViewModel = koinViewModel<SettingsViewModel>()

        WeatherAppNav(
            weatherViewModel = viewModel,
            settingsViewModel = settingsViewModel,
        )
    }
}

@Composable
fun WeatherAppNav(
    weatherViewModel: WeatherViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "weather"
    ) {
        composable("weather") {
            WeatherScreen(
                weatherViewModel = weatherViewModel,
                //locationService = locationService,
                //prefs = prefs,
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}


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

