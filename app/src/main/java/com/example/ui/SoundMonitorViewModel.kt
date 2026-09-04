package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioAnalyzer
import com.example.model.TriggerMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class SoundMonitorUiState(
    val isMonitoring: Boolean = false,
    val permissionGranted: Boolean = false,
    val currentDb: Float = 0f,
    val peakFrequencyHz: Float = 0f,
    val dbThreshold: Float = 65f,
    val khzThreshold: Float = 2.0f,
    val triggerMode: TriggerMode = TriggerMode.DB_ONLY,
    val alarmCount: Int = 0,
    val elapsedSeconds: Long = 0L,
    val maxDb: Float = 0f,
    val minDb: Float = 0f,
    val waveHistory: List<Float> = List(60) { 0f },
    val spectrumBands: List<Float> = List(32) { 0.05f },
    val isAlarmFlashing: Boolean = false
)

class SoundMonitorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SoundMonitorUiState())
    val uiState: StateFlow<SoundMonitorUiState> = _uiState.asStateFlow()

    private var audioJob: Job? = null
    private var timerJob: Job? = null
    private var flashJob: Job? = null

    private val audioAnalyzer = AudioAnalyzer(
        onAlarmTriggered = {
            onAlarmOccurred()
        }
    )

    private fun onAlarmOccurred() {
        _uiState.update { current ->
            current.copy(
                alarmCount = current.alarmCount + 1,
                isAlarmFlashing = true
            )
        }
        flashJob?.cancel()
        flashJob = viewModelScope.launch {
            delay(700)
            _uiState.update { it.copy(isAlarmFlashing = false) }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(permissionGranted = granted) }
        if (granted && !_uiState.value.isMonitoring) {
            startMonitoring()
        }
    }

    fun startMonitoring() {
        if (!audioAnalyzer.startListening()) {
            return
        }

        _uiState.update { it.copy(isMonitoring = true) }

        // Start timer job
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                if (_uiState.value.isMonitoring) {
                    _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }

        // Start real-time audio sampling job
        audioJob?.cancel()
        audioJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive && _uiState.value.isMonitoring) {
                val state = _uiState.value
                val frame = audioAnalyzer.readAudioFrame(
                    dbThreshold = state.dbThreshold,
                    khzThreshold = state.khzThreshold,
                    triggerMode = state.triggerMode
                )

                _uiState.update { current ->
                    val newHistory = current.waveHistory.drop(1) + frame.currentDb
                    val newMax = if (current.maxDb == 0f) frame.currentDb else maxOf(current.maxDb, frame.currentDb)
                    val newMin = if (current.minDb == 0f && frame.currentDb > 0f) frame.currentDb else if (frame.currentDb > 0f) minOf(current.minDb, frame.currentDb) else current.minDb

                    current.copy(
                        currentDb = frame.currentDb,
                        peakFrequencyHz = frame.peakFrequencyHz,
                        waveHistory = newHistory,
                        spectrumBands = frame.spectrumBands,
                        maxDb = newMax,
                        minDb = newMin
                    )
                }

                // Short throttle to maintain smooth ~30 fps UI updates without CPU overload
                delay(30)
            }
        }
    }

    fun pauseMonitoring() {
        audioJob?.cancel()
        audioJob = null
        timerJob?.cancel()
        timerJob = null
        audioAnalyzer.stopListening()
        _uiState.update { it.copy(isMonitoring = false) }
    }

    fun toggleMonitoring() {
        if (_uiState.value.isMonitoring) {
            pauseMonitoring()
        } else {
            startMonitoring()
        }
    }

    fun resetStatistics() {
        _uiState.update {
            it.copy(
                alarmCount = 0,
                elapsedSeconds = 0L,
                maxDb = 0f,
                minDb = 0f
            )
        }
    }

    fun setDbThreshold(value: Float) {
        val clamped = value.coerceIn(30f, 95f)
        _uiState.update { it.copy(dbThreshold = clamped) }
    }

    fun adjustDbThreshold(delta: Float) {
        setDbThreshold(_uiState.value.dbThreshold + delta)
    }

    fun setKhzThreshold(value: Float) {
        val clamped = (Math.round(value.coerceIn(0.2f, 16.0f) * 10) / 10f)
        _uiState.update { it.copy(khzThreshold = clamped) }
    }

    fun adjustKhzThreshold(delta: Float) {
        setKhzThreshold(_uiState.value.khzThreshold + delta)
    }

    fun setTriggerMode(mode: TriggerMode) {
        _uiState.update { it.copy(triggerMode = mode) }
    }

    fun testBeep() {
        audioAnalyzer.playBeep()
    }

    override fun onCleared() {
        super.onCleared()
        audioAnalyzer.release()
    }
}
