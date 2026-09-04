package com.example.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.ToneGenerator
import android.util.Log
import com.example.model.AudioAnalysisResult
import com.example.model.TriggerMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt

class AudioAnalyzer(
    private val onAlarmTriggered: () -> Unit
) {
    companion object {
        const val SAMPLE_RATE = 44100
        const val FFT_SIZE = 1024
        private const val TAG = "AudioAnalyzer"
    }

    private var audioRecord: AudioRecord? = null
    private var toneGenerator: ToneGenerator? = null
    private var isRecording = false
    private var lastAlarmTime = 0L
    private val alarmCooldownMs = 800L

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        } catch (e: Exception) {
            Log.e(TAG, "ToneGenerator initialization error", e)
        }
    }

    fun startListening(): Boolean {
        try {
            val minBufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(minBufferSize, FFT_SIZE * 2)

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord failed to initialize")
                return false
            }

            audioRecord?.startRecording()
            isRecording = true
            return true
        } catch (e: SecurityException) {
            Log.e(TAG, "Microphone permission not granted", e)
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start AudioRecord", e)
            return false
        }
    }

    fun stopListening() {
        isRecording = false
        try {
            if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                audioRecord?.stop()
            }
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping AudioRecord", e)
        } finally {
            audioRecord = null
        }
    }

    fun playBeep() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 250)
        } catch (e: Exception) {
            Log.e(TAG, "Error playing beep tone", e)
        }
    }

    suspend fun readAudioFrame(
        dbThreshold: Float,
        khzThreshold: Float,
        triggerMode: TriggerMode
    ): AudioAnalysisResult = withContext(Dispatchers.Default) {
        val recorder = audioRecord
        if (recorder == null || !isRecording) {
            return@withContext AudioAnalysisResult()
        }

        val buffer = ShortArray(FFT_SIZE)
        val readCount = recorder.read(buffer, 0, FFT_SIZE)
        if (readCount <= 0) {
            return@withContext AudioAnalysisResult()
        }

        // 1. Calculate RMS and dB
        var sumSquares = 0.0
        var maxSample = 0
        for (i in 0 until readCount) {
            val sample = buffer[i].toInt()
            sumSquares += sample * sample
            val absSample = abs(sample)
            if (absSample > maxSample) maxSample = absSample
        }
        val rms = sqrt(sumSquares / readCount)
        val rawDb = if (rms > 1.0) (20 * log10(rms)).toFloat() else 0f
        // Calibrate dB to realistic 15 - 95 dB range
        val currentDb = rawDb.coerceIn(0f, 95f)

        // 2. Perform FFT for Frequency analysis
        val real = FloatArray(FFT_SIZE)
        val imag = FloatArray(FFT_SIZE)
        for (i in 0 until FFT_SIZE) {
            val window = 0.5 * (1 - cos(2.0 * PI * i / (FFT_SIZE - 1)))
            real[i] = (if (i < readCount) buffer[i].toFloat() else 0f) * window.toFloat()
            imag[i] = 0f
        }

        fft(real, imag)

        // Find peak frequency and build 32 spectrum bands
        val halfSize = FFT_SIZE / 2
        var peakMag = 0f
        var peakBin = 0
        val magnitudes = FloatArray(halfSize)

        for (i in 2 until halfSize) { // Ignore DC bias (< 80 Hz)
            val mag = sqrt(real[i] * real[i] + imag[i] * imag[i])
            magnitudes[i] = mag
            if (mag > peakMag) {
                peakMag = mag
                peakBin = i
            }
        }

        val binFreq = SAMPLE_RATE.toFloat() / FFT_SIZE
        val peakFrequencyHz = if (peakMag > 350f && currentDb > 20f) {
            peakBin * binFreq
        } else {
            0f
        }
        val peakFrequencyKhz = peakFrequencyHz / 1000f

        // Partition into 32 frequency bands for visualization
        val numBands = 32
        val spectrumBands = ArrayList<Float>(numBands)
        val binsPerBand = (halfSize - 2) / numBands
        for (b in 0 until numBands) {
            var bandSum = 0f
            val start = 2 + b * binsPerBand
            val end = (start + binsPerBand).coerceAtMost(halfSize)
            for (k in start until end) {
                bandSum += magnitudes[k]
            }
            val avg = if (end > start) bandSum / (end - start) else 0f
            val normalized = (avg / 25000f).coerceIn(0.04f, 1f)
            spectrumBands.add(normalized)
        }

        // 3. Threshold evaluation
        val isDbCrossed = currentDb >= dbThreshold
        val isKhzCrossed = peakFrequencyKhz >= khzThreshold && currentDb >= 30f

        val shouldAlarm = when (triggerMode) {
            TriggerMode.DB_ONLY -> isDbCrossed
            TriggerMode.KHZ_ONLY -> isKhzCrossed
            TriggerMode.BOTH -> isDbCrossed && isKhzCrossed
        }

        var isAlarmTriggered = false
        if (shouldAlarm) {
            val now = System.currentTimeMillis()
            if (now - lastAlarmTime >= alarmCooldownMs) {
                lastAlarmTime = now
                isAlarmTriggered = true
                playBeep()
                onAlarmTriggered()
            }
        }

        AudioAnalysisResult(
            currentDb = currentDb,
            peakFrequencyHz = peakFrequencyHz,
            waveSample = currentDb,
            spectrumBands = spectrumBands,
            isThresholdCrossed = isAlarmTriggered
        )
    }

    private fun fft(real: FloatArray, imag: FloatArray) {
        val n = real.size
        var j = 0
        for (i in 0 until n - 1) {
            if (i < j) {
                val tempR = real[i]; real[i] = real[j]; real[j] = tempR
                val tempI = imag[i]; imag[i] = imag[j]; imag[j] = tempI
            }
            var k = n shr 1
            while (k <= j) {
                j -= k
                k = k shr 1
            }
            j += k
        }

        var l = 2
        while (l <= n) {
            val deltaAngle = -2.0 * PI / l
            val wStepR = cos(deltaAngle).toFloat()
            val wStepI = sin(deltaAngle).toFloat()

            var i = 0
            while (i < n) {
                var wR = 1.0f
                var wI = 0.0f
                val halfL = l shr 1
                for (m in 0 until halfL) {
                    val uR = real[i + m]
                    val uI = imag[i + m]
                    val vR = real[i + m + halfL] * wR - imag[i + m + halfL] * wI
                    val vI = real[i + m + halfL] * wI + imag[i + m + halfL] * wR

                    real[i + m] = uR + vR
                    imag[i + m] = uI + vI
                    real[i + m + halfL] = uR - vR
                    imag[i + m + halfL] = uI - vI

                    val nextWR = wR * wStepR - wI * wStepI
                    val nextWI = wR * wStepI + wI * wStepR
                    wR = nextWR
                    wI = nextWI
                }
                i += l
            }
            l = l shl 1
        }
    }

    fun release() {
        stopListening()
        try {
            toneGenerator?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing ToneGenerator", e)
        } finally {
            toneGenerator = null
        }
    }
}
