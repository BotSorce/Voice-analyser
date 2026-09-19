package com.example

import com.example.model.AudioAnalysisResult
import com.example.model.MonitorStats
import com.example.model.TriggerMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun audioAnalysisResult_defaultValues() {
    val result = AudioAnalysisResult()
    assertEquals(0f, result.currentDb, 0.001f)
    assertEquals(0f, result.peakFrequencyHz, 0.001f)
    assertFalse(result.isThresholdCrossed)
    assertTrue(result.spectrumBands.isEmpty())
  }

  @Test
  fun monitorStats_initialState() {
    val stats = MonitorStats(alarmCount = 3, elapsedSeconds = 120L, maxDb = 85.5f, minDb = 32.0f)
    assertEquals(3, stats.alarmCount)
    assertEquals(120L, stats.elapsedSeconds)
    assertEquals(85.5f, stats.maxDb, 0.01f)
    assertEquals(32.0f, stats.minDb, 0.01f)
  }

  @Test
  fun triggerMode_enumCheck() {
    assertEquals(3, TriggerMode.entries.size)
    assertTrue(TriggerMode.entries.contains(TriggerMode.DB_ONLY))
    assertTrue(TriggerMode.entries.contains(TriggerMode.KHZ_ONLY))
    assertTrue(TriggerMode.entries.contains(TriggerMode.BOTH))
  }
}
