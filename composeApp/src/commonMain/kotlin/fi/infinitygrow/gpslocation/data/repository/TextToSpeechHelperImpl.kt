package fi.infinitygrow.gpslocation.data.repository

import fi.infinitygrow.gpslocation.domain.repository.TextToSpeechHelper

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class TextToSpeechHelperImpl : TextToSpeechHelper {
    override fun speak(text: String)

    override fun stop()

    override fun destroy()
}