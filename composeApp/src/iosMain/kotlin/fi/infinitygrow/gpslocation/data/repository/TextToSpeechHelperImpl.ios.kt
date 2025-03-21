package fi.infinitygrow.gpslocation.data.repository

import fi.infinitygrow.gpslocation.domain.repository.TextToSpeechHelper
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechBoundary

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class TextToSpeechHelperImpl : TextToSpeechHelper {
    private val synthesizer = AVSpeechSynthesizer()

    actual override fun speak(text: String) {
        val utterance = AVSpeechUtterance.speechUtteranceWithString(text)
        utterance.rate = 0.5f // Normal speaking rate
        utterance.voice = AVSpeechSynthesisVoice.voiceWithLanguage("fi-FI")

//        AVSpeechSynthesisVoice.speechVoices().forEach {
//            println("Voice available: ${it})")
//        }

        synthesizer.speakUtterance(utterance)
    }

    actual override fun stop() {
        synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
    }

    actual override fun destroy() {
        TODO("Not yet implemented")
    }

}