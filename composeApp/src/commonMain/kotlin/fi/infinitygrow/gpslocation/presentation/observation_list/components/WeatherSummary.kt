package fi.infinitygrow.gpslocation.presentation.observation_list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.infinitygrow.gpslocation.domain.model.Weather
import fi.infinitygrow.gpslocation.presentation.utils.formatValue
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter


@Composable
fun WeatherSummary(
    currentWeather: Weather?, // Your weather model type
    isDarkTheme: Boolean
) {
    val fontColor = if (isDarkTheme) Color.Black else Color.White

    val painter = rememberAsyncImagePainter(model = currentWeather?.iconUrl)

    currentWeather?.let { weather ->
        Spacer(modifier = Modifier.height(32.dp))
        Column(modifier = Modifier
            .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Image(painter, contentDescription = "weather symbol", modifier = Modifier.size(80.dp))
                Text(
                    text = "${formatValue(weather.temperature.toFloat())} °C",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 60.sp,
                        color = fontColor
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

            }
            Text(
                text = weather.name,
                style = MaterialTheme.typography.headlineMedium.copy(color = fontColor)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}