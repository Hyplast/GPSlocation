package fi.infinitygrow.gpslocation.presentation.observation_list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gpslocation.composeapp.generated.resources.Res
import gpslocation.composeapp.generated.resources.circlearrow_blue
import gpslocation.composeapp.generated.resources.circlearrow_green
import gpslocation.composeapp.generated.resources.circlearrow_yellow
import gpslocation.composeapp.generated.resources.circlearroworange
import gpslocation.composeapp.generated.resources.circlearrowpurple
import gpslocation.composeapp.generated.resources.circlearrowred
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun CompassArrow(bearing: Double, speed: Double, gust: Double) {
    // Convert the number to an integer safely.
    val numValue = (gust - speed).roundToInt()

    // Select the image resource based on the number value.
    val imageRes = when (numValue) {
        1 -> Res.drawable.circlearrow_blue
        2 -> Res.drawable.circlearrow_green
        3 -> Res.drawable.circlearrow_yellow
        4 -> Res.drawable.circlearroworange
        5 -> Res.drawable.circlearrowred
        6 -> Res.drawable.circlearrowpurple
        else -> Res.drawable.circlearrow_green // Fallback resource
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .background(Color.Transparent) // CircleShape)
            //.border(1.dp, Color.Gray, CircleShape) // Optional: Add a border for better visibility
    ) {
        // Arrow Image
        Image(
            painter = painterResource(imageRes),
            contentDescription = "Wind direction arrow",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    rotationZ = (bearing + 90f).toFloat(),
                    rotationX = 0.5f,
                    rotationY = 0.5f
                )
        )

        // Number Overlay
        Text(
            text = speed.roundToInt().toString(),
            color = Color.Black, // Change color as needed
            fontSize = 17.sp, // Adjust font size as needed
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center) // Center the text over the arrow
                .padding(4.dp) // Optional: Add padding for better spacing
        )
    }
}