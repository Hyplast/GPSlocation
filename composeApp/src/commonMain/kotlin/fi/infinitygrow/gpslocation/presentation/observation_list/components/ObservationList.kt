package fi.infinitygrow.gpslocation.presentation.observation_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fi.infinitygrow.gpslocation.core.presentation.LeafGreenColor
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.presentation.observation_list.WeatherViewModel
import fi.infinitygrow.gpslocation.presentation.utils.constructLanguageString


@Composable
fun ObservationsList(
    observations: List<ObservationData>, // your model list
    viewModel: WeatherViewModel,
    isDarkTheme: Boolean
) {
    LazyColumn(
        modifier = Modifier
            // change background based on theme if needed
            .background(if (isDarkTheme) Color.Black else Color.White)
            .fillMaxSize()
    ) {
        items(viewModel.getNewestObservations(observations)) { observation ->
            val isLongPressed = viewModel.longPressedItems.contains(observation)
            val backgroundColor = if (isLongPressed) LeafGreenColor else Color.White

            // Construct rows of data, check for NaN values, etc.
            // ... (you can even extract an ObservationCard composable for this)
            ObservationCard(
                observation = observation,
                isLongPressed = isLongPressed,
                onShortPress = { /* update short pressed location */ },
                onLongPress = {
                    viewModel.toggleLongPress(observation)
                    // update long pressed location if needed
                }
            )
            constructLanguageString(observation, location = observation.coordinates)?.let { langString ->
                Text(
                    text = langString,
                    modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                )
            }
        }
    }
}