package za.kilowatch.hawkeyetvbrowser.core.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import za.kilowatch.hawkeyetvbrowser.R
import javax.inject.Inject
import javax.inject.Singleton

sealed class VoiceState {
    object Idle : VoiceState()
    object Listening : VoiceState()
    object Processing : VoiceState()
    data class Success(val recognizedText: String) : VoiceState()
    data class Error(val message: String) : VoiceState()
}

@Singleton
class VoiceSearchService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _state = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val state: StateFlow<VoiceState> = _state.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null

    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening() {
        if (!isAvailable()) {
            _state.value = VoiceState.Error(context.getString(R.string.voice_search_unsupported))
            return
        }

        stopListening() // Cleanup previous instance if any

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _state.value = VoiceState.Listening
                }

                override fun onBeginningOfSpeech() {
                    _state.value = VoiceState.Listening
                }

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    _state.value = VoiceState.Processing
                }

                override fun onError(error: Int) {
                    val message = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> context.getString(R.string.voice_error_audio)
                        SpeechRecognizer.ERROR_CLIENT -> context.getString(R.string.voice_error_client)
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> context.getString(R.string.voice_error_permissions)
                        SpeechRecognizer.ERROR_NETWORK -> context.getString(R.string.voice_error_network)
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> context.getString(R.string.voice_error_network_timeout)
                        SpeechRecognizer.ERROR_NO_MATCH -> context.getString(R.string.voice_error_no_match)
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> context.getString(R.string.voice_error_busy)
                        SpeechRecognizer.ERROR_SERVER -> context.getString(R.string.voice_error_server)
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> context.getString(R.string.voice_error_speech_timeout)
                        else -> context.getString(R.string.voice_error_generic, error)
                    }
                    _state.value = VoiceState.Error(message)
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val topMatch = matches?.firstOrNull()
                    if (!topMatch.isNullOrBlank()) {
                        _state.value = VoiceState.Success(topMatch)
                    } else {
                        _state.value = VoiceState.Error("No match found")
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Search or say a command...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _state.value = VoiceState.Error(e.localizedMessage ?: "Failed to start voice listener")
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    fun resetState() {
        stopListening()
        _state.value = VoiceState.Idle
    }
}
