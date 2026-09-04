package com.example.model

import com.example.R

enum class TriggerMode(val titleResId: Int) {
    DB_ONLY(R.string.trigger_db),
    KHZ_ONLY(R.string.trigger_khz),
    BOTH(R.string.trigger_both)
}

data class AudioAnalysisResult(
    val currentDb: Float = 0f,
    val peakFrequencyHz: Float = 0f,
    val waveSample: Float = 0f,
    val spectrumBands: List<Float> = emptyList(),
    val isThresholdCrossed: Boolean = false
)

data class MonitorStats(
    val alarmCount: Int = 0,
    val elapsedSeconds: Long = 0L,
    val maxDb: Float = 0f,
    val minDb: Float = 0f
)
