package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AudioAmber
import com.example.ui.theme.AudioCardBorder
import com.example.ui.theme.AudioCyan
import com.example.ui.theme.AudioDarkNavy
import com.example.ui.theme.AudioDarkSurface
import com.example.ui.theme.AudioGreen
import com.example.ui.theme.AudioRedAlert
import com.example.ui.theme.AudioTextPrimary
import com.example.ui.theme.AudioTextSecondary
import com.example.ui.theme.ElegantAmber
import com.example.ui.theme.ElegantCoral
import com.example.ui.theme.ElegantCoralActive
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceInset
import com.example.ui.theme.ElegantGreen
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantOnCoral
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary

@Composable
fun AlarmAndTimerCard(
    alarmCount: Int,
    elapsedSeconds: Long,
    isAlarmActive: Boolean,
    isMonitoring: Boolean,
    modifier: Modifier = Modifier
) {
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("alarm_and_timer_card")
            .border(
                width = if (isAlarmActive) 2.dp else 1.dp,
                color = if (isAlarmActive) ElegantCoral else ElegantDarkOutline,
                shape = RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAlarmActive) Color(0xFF381F21) else ElegantDarkSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Alarm banner if currently sounding
            AnimatedVisibility(
                visible = isAlarmActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ElegantCoral)
                        .padding(vertical = 8.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = stringResource(R.string.alarm_active_alert),
                        tint = ElegantOnCoral,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.alarm_active_alert),
                        color = ElegantOnCoral,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isAlarmActive) {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Grid of 2 inner cards (Total Alarms & Uptime)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Total Alarms Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(ElegantDarkSurfaceInset)
                        .border(1.dp, Color(0x3344474E), RoundedCornerShape(20.dp))
                        .padding(14.dp)
                        .testTag("alarm_counter_badge")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "TOTAL ALARMS • هشدارها",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp,
                            color = ElegantTextMuted
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ElegantCoral)
                            )
                            Text(
                                text = "$alarmCount",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantCoral
                            )
                        }
                        Text(
                            text = "تعداد عبور از حد مجاز",
                            fontSize = 11.sp,
                            color = ElegantTextSecondary.copy(alpha = 0.7f)
                        )
                    }
                }

                // 2. Uptime Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(ElegantDarkSurfaceInset)
                        .border(1.dp, Color(0x3344474E), RoundedCornerShape(20.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "UPTIME • مدت پایش",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp,
                            color = ElegantTextMuted
                        )
                        Text(
                            text = formattedTime,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantTextPrimary,
                            modifier = Modifier.testTag("elapsed_timer_text")
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isMonitoring) ElegantGreen else ElegantAmber)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isMonitoring) "پایش فعال" else "متوقف",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isMonitoring) ElegantGreen else ElegantAmber
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RealtimeMetricsCard(
    currentDb: Float,
    peakFrequencyHz: Float,
    maxDb: Float,
    modifier: Modifier = Modifier
) {
    val levelColor = when {
        currentDb >= 75f -> ElegantCoral
        currentDb >= 55f -> ElegantAmber
        else -> ElegantGreen
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Sound Level Card (dB)
        Card(
            modifier = Modifier
                .weight(1f)
                .testTag("metric_card_db"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkOutline)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.current_sound_level),
                        style = MaterialTheme.typography.labelSmall,
                        color = ElegantTextSecondary
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = levelColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${currentDb.toInt()}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = levelColor
                    )
                    Text(
                        text = " dB",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElegantTextSecondary,
                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { (currentDb / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = levelColor,
                    trackColor = ElegantDarkOutline,
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "حداکثر: ${maxDb.toInt()} dB",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElegantTextMuted
                )
            }
        }

        // Peak Frequency Card (kHz)
        Card(
            modifier = Modifier
                .weight(1f)
                .testTag("metric_card_khz"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkOutline)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.peak_frequency),
                        style = MaterialTheme.typography.labelSmall,
                        color = ElegantTextSecondary
                    )
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = ElegantLavender,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    val khz = peakFrequencyHz / 1000f
                    Text(
                        text = String.format("%.2f", khz),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = ElegantLavender
                    )
                    Text(
                        text = " kHz",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElegantTextSecondary,
                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { (peakFrequencyHz / 16000f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ElegantLavender,
                    trackColor = ElegantDarkOutline,
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${peakFrequencyHz.toInt()} Hz",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElegantTextMuted
                )
            }
        }
    }
}
