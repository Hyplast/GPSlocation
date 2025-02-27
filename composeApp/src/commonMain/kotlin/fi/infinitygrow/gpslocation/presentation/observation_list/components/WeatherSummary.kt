package fi.infinitygrow.gpslocation.presentation.observation_list.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.infinitygrow.gpslocation.domain.model.Weather
import fi.infinitygrow.gpslocation.presentation.utils.formatValue

@Composable
fun WeatherSummary(
    currentWeather: Weather?, // Your weather model type
    isDarkTheme: Boolean
) {
    currentWeather?.let { weather ->
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "${formatValue(weather.temperature.toFloat())} °C",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 60.sp, color = Color.White)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = weather.name,
            style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}