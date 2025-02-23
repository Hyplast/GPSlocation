package fi.infinitygrow.gpslocation

import androidx.compose.ui.window.ComposeUIViewController
import fi.infinitygrow.gpslocation.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }