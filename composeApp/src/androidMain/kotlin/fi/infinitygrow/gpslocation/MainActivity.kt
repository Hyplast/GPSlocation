package fi.infinitygrow.gpslocation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fi.infinitygrow.gpslocation.app.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

