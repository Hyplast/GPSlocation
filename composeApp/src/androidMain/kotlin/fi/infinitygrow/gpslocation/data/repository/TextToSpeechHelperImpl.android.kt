package fi.infinitygrow.gpslocation.data.repository

import android.content.Context
import android.speech.tts.TextToSpeech
import fi.infinitygrow.gpslocation.domain.repository.TextToSpeechHelper
import java.util.Locale

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class TextToSpeechHelperImpl(private val context: Context) : TextToSpeechHelper {
    private var textToSpeech: TextToSpeech? = null
    private var initialized = false

    init {
        textToSpeech = TextToSpeech(context) { status ->
            initialized = status == TextToSpeech.SUCCESS
            if (initialized) {
                //textToSpeech?.language = Locale.US
                val deviceLocale = Locale.getDefault() // Get the phone's current language
                textToSpeech?.language = deviceLocale
                //textToSpeech?.language = Locale("fi", "FI") // Using Finnish since your text is in Finnish
            }
        }
    }

    actual override fun speak(text: String) {
        if (initialized) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    actual override fun stop() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    actual override fun destroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

}