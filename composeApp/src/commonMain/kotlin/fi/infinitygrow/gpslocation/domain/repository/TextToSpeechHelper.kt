package fi.infinitygrow.gpslocation.domain.repository

interface TextToSpeechHelper {
    fun speak(text: String)
    fun stop()
    fun destroy()
}