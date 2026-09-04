package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AudioCardBorder
import com.example.ui.theme.AudioCyan
import com.example.ui.theme.AudioDarkNavy
import com.example.ui.theme.AudioDarkSurface
import com.example.ui.theme.AudioRedAlert
import com.example.ui.theme.AudioTextPrimary
import com.example.ui.theme.AudioTextSecondary
import com.example.ui.theme.ElegantCoral
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceInset
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantOnCoral
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary

@Composable
fun SoundGraph(
    waveHistory: List<Float>,
    spectrumBands: List<Float>,
    dbThreshold: Float,
    isAlarmTriggered: Boolean,
    currentDb: Float,
    peakFrequencyHz: Float,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isAlarmTriggered) ElegantCoral else ElegantDarkOutline,
        animationSpec = tween(durationMillis = 250),
        label = "graphBorder"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(ElegantDarkSurface)
            .border(1.dp, borderColor, RoundedCornerShape(28.dp))
            .padding(18.dp)
    ) {
        // Top info header in graph matching Elegant Dark design
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LOUDNESS • نمودار زنده طیف",
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElegantLavender
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${currentDb.toInt()}",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Monospace,
                        color = ElegantTextPrimary
                    )
                    Text(
                        text = " dB",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = ElegantTextSecondary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "PEAK FREQUENCY",
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp,
                    color = ElegantTextMuted
                )
                Text(
                    text = String.format("%.1f kHz", peakFrequencyHz / 1000f),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isAlarmTriggered) ElegantCoral else ElegantLavender
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live Audio Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(ElegantDarkSurfaceInset)
                .border(1.dp, Color(0x3344474E), RoundedCornerShape(20.dp))
                .testTag("sound_graph_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // 1. Draw Grid Lines (at 25%, 50%, 75% height)
                val gridColor = Color(0x18FFFFFF)
                val gridLevels = listOf(0.25f, 0.50f, 0.75f)
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

                for (ratio in gridLevels) {
                    val y = canvasHeight * ratio
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f,
                        pathEffect = dashEffect
                    )
                }

                // 2. Draw Spectrum Equalizer Bars
                if (spectrumBands.isNotEmpty()) {
                    val barSpacing = 2.5f
                    val totalSpacing = barSpacing * (spectrumBands.size - 1)
                    val barWidth = ((canvasWidth - totalSpacing) / spectrumBands.size).coerceAtLeast(1f)

                    for (i in spectrumBands.indices) {
                        val bandHeight = (spectrumBands[i] * canvasHeight * 0.85f).coerceIn(4f, canvasHeight)
                        val left = i * (barWidth + barSpacing)
                        val top = canvasHeight - bandHeight

                        val barBrush = Brush.verticalGradient(
                            colors = if (isAlarmTriggered) {
                                listOf(ElegantCoral, Color(0x33F2B8B5))
                            } else {
                                listOf(ElegantLavender, Color(0x22D0BCFF))
                            },
                            startY = top,
                            endY = canvasHeight
                        )

                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(left, top),
                            size = Size(barWidth, bandHeight),
                            cornerRadius = CornerRadius(2f, 2f)
                        )
                    }
                }

                // 3. Draw Threshold Line (scaled based on 0 to 100 dB)
                val thresholdRatio = (dbThreshold / 100f).coerceIn(0f, 1f)
                val thresholdY = canvasHeight * (1f - thresholdRatio)

                drawLine(
                    color = ElegantCoral,
                    start = Offset(0f, thresholdY),
                    end = Offset(canvasWidth, thresholdY),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                )

                // 4. Draw Rolling Waveform Path with glowing gradient
                if (waveHistory.size >= 2) {
                    val stepX = canvasWidth / (waveHistory.size - 1).toFloat()
                    val wavePath = Path()
                    val fillPath = Path()

                    val startY = canvasHeight * (1f - (waveHistory[0] / 100f).coerceIn(0f, 1f))
                    wavePath.moveTo(0f, startY)
                    fillPath.moveTo(0f, canvasHeight)
                    fillPath.lineTo(0f, startY)

                    for (i in 1 until waveHistory.size) {
                        val x = i * stepX
                        val y = canvasHeight * (1f - (waveHistory[i] / 100f).coerceIn(0f, 1f))
                        wavePath.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }

                    fillPath.lineTo(canvasWidth, canvasHeight)
                    fillPath.close()

                    // Wave translucent fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0x33D0BCFF), Color(0x05D0BCFF)),
                            startY = 0f,
                            endY = canvasHeight
                        )
                    )

                    // Wave stroke
                    drawPath(
                        path = wavePath,
                        color = if (isAlarmTriggered) ElegantCoral else ElegantLavender,
                        style = Stroke(width = 3f)
                    )
                }
            }

            // Threshold Badge overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xDDF2B8B5))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "آستانه: ${dbThreshold.toInt()} dB",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantOnCoral
                )
            }
        }
    }
}
