package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.TriggerMode
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
import com.example.ui.theme.ElegantLavenderContainer
import com.example.ui.theme.ElegantOnCoral
import com.example.ui.theme.ElegantOnLavender
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThresholdControlsCard(
    dbThreshold: Float,
    khzThreshold: Float,
    triggerMode: TriggerMode,
    onDbChanged: (Float) -> Unit,
    onDbAdjust: (Float) -> Unit,
    onKhzChanged: (Float) -> Unit,
    onKhzAdjust: (Float) -> Unit,
    onModeChanged: (TriggerMode) -> Unit,
    onTestBeep: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("threshold_controls_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
        border = BorderStroke(1.dp, ElegantDarkOutline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(ElegantLavender.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = ElegantLavender,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "SENSITIVITY & TRIGGER",
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElegantLavender
                    )
                    Text(
                        text = "تنظیمات آستانه شنوایی و ماشه هشدار",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trigger Mode Chips
            Text(
                text = stringResource(R.string.trigger_mode_title),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = ElegantTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TriggerMode.values().forEach { mode ->
                    val selected = triggerMode == mode
                    FilterChip(
                        selected = selected,
                        onClick = { onModeChanged(mode) },
                        label = {
                            Text(
                                text = stringResource(mode.titleResId),
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElegantLavender,
                            selectedLabelColor = ElegantOnLavender,
                            containerColor = ElegantDarkSurfaceInset,
                            labelColor = ElegantTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = if (selected) ElegantLavender else ElegantDarkOutline,
                            borderWidth = 1.dp
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("chip_${mode.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Decibel Slider (Sensitivity Threshold in Elegant Dark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Hearing,
                        contentDescription = null,
                        tint = ElegantCoral,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.sound_threshold),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = ElegantTextPrimary
                    )
                }

                Text(
                    text = "${dbThreshold.toInt()} dB",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantCoral
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onDbAdjust(-1f) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ElegantDarkSurfaceInset)
                        .border(1.dp, Color(0x3344474E), CircleShape)
                        .testTag("db_minus_btn")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "کاهش ۱ دسی‌بل", tint = ElegantTextSecondary)
                }

                Slider(
                    value = dbThreshold,
                    onValueChange = onDbChanged,
                    valueRange = 30f..95f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElegantCoral,
                        activeTrackColor = ElegantCoral,
                        inactiveTrackColor = ElegantDarkOutline
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .testTag("db_slider")
                )

                IconButton(
                    onClick = { onDbAdjust(1f) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ElegantDarkSurfaceInset)
                        .border(1.dp, Color(0x3344474E), CircleShape)
                        .testTag("db_plus_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "افزایش ۱ دسی‌بل", tint = ElegantTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Frequency (kHz) Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = ElegantLavender,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.frequency_threshold),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = ElegantTextPrimary
                    )
                }

                Text(
                    text = String.format("%.1f kHz", khzThreshold),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantLavender
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onKhzAdjust(-0.1f) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ElegantDarkSurfaceInset)
                        .border(1.dp, Color(0x3344474E), CircleShape)
                        .testTag("khz_minus_btn")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "کاهش ۰.۱ کیلوهرتز", tint = ElegantTextSecondary)
                }

                Slider(
                    value = khzThreshold,
                    onValueChange = onKhzChanged,
                    valueRange = 0.2f..16.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElegantLavender,
                        activeTrackColor = ElegantLavender,
                        inactiveTrackColor = ElegantDarkOutline
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .testTag("khz_slider")
                )

                IconButton(
                    onClick = { onKhzAdjust(0.1f) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ElegantDarkSurfaceInset)
                        .border(1.dp, Color(0x3344474E), CircleShape)
                        .testTag("khz_plus_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "افزایش ۰.۱ کیلوهرتز", tint = ElegantTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Test Beep Button
            OutlinedButton(
                onClick = onTestBeep,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("test_beep_button"),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, ElegantLavender.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = ElegantDarkSurfaceInset,
                    contentColor = ElegantLavender
                )
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.btn_test_beep),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ActionControls(
    isMonitoring: Boolean,
    onToggleMonitoring: () -> Unit,
    onResetStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Start / Stop Button - Pill style matching Elegant Dark
            Button(
                onClick = onToggleMonitoring,
                modifier = Modifier
                    .weight(2f)
                    .height(56.dp)
                    .testTag("toggle_monitoring_button"),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMonitoring) ElegantCoral else ElegantLavender,
                    contentColor = if (isMonitoring) ElegantOnCoral else ElegantOnLavender
                )
            ) {
                Icon(
                    imageVector = if (isMonitoring) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isMonitoring) ElegantOnCoral else ElegantOnLavender,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(if (isMonitoring) R.string.btn_pause else R.string.btn_start),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMonitoring) ElegantOnCoral else ElegantOnLavender
                )
            }

            // Reset Stats Button
            Button(
                onClick = onResetStats,
                modifier = Modifier
                    .weight(1.2f)
                    .height(56.dp)
                    .testTag("reset_stats_button"),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, ElegantDarkOutline),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElegantDarkSurfaceInset,
                    contentColor = ElegantTextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.btn_reset),
                    tint = ElegantTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.btn_reset),
                    fontSize = 14.sp,
                    color = ElegantTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status caption from Elegant Dark layout
        Text(
            text = if (isMonitoring) "ACTIVE • RECORDING IN PROGRESS" else "MONITORING PAUSED • READY",
            fontSize = 10.sp,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Medium,
            color = ElegantTextMuted,
            textAlign = TextAlign.Center
        )
    }
}
