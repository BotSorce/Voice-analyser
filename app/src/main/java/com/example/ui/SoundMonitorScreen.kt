package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.ActionControls
import com.example.ui.components.AlarmAndTimerCard
import com.example.ui.components.RealtimeMetricsCard
import com.example.ui.components.SoundGraph
import com.example.ui.components.ThresholdControlsCard
import com.example.ui.theme.AudioAmber
import com.example.ui.theme.AudioCyan
import com.example.ui.theme.AudioDarkNavy
import com.example.ui.theme.AudioDarkSurface
import com.example.ui.theme.AudioGreen
import com.example.ui.theme.AudioRedAlert
import com.example.ui.theme.AudioTextPrimary
import com.example.ui.theme.AudioTextSecondary
import com.example.ui.theme.ElegantAmber
import com.example.ui.theme.ElegantCoral
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceInset
import com.example.ui.theme.ElegantGreen
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantOnCoral
import com.example.ui.theme.ElegantOnLavender
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary

@Composable
fun SoundMonitorScreen(
    viewModel: SoundMonitorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
    }

    // Auto-check and request permission on launch
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.onPermissionResult(hasPermission)
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = statusBarPadding, bottom = navBarPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Bar with circular lavender badge matching Elegant Dark
                HeaderBar(
                    isMonitoring = uiState.isMonitoring,
                    permissionGranted = uiState.permissionGranted,
                    isDarkTheme = uiState.isDarkTheme,
                    onToggleTheme = { viewModel.toggleTheme() }
                )

                // Permission Warning Banner if denied
                AnimatedVisibility(
                    visible = !uiState.permissionGranted,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    PermissionCard(
                        onRequestPermission = {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    )
                }

                // 1. Real-time Sound Graph & Equalizer
                SoundGraph(
                    waveHistory = uiState.waveHistory,
                    spectrumBands = uiState.spectrumBands,
                    dbThreshold = uiState.dbThreshold,
                    isAlarmTriggered = uiState.isAlarmFlashing,
                    currentDb = uiState.currentDb,
                    peakFrequencyHz = uiState.peakFrequencyHz
                )

                // 2. Real-time Metrics (Current dB & Peak Frequency kHz)
                RealtimeMetricsCard(
                    currentDb = uiState.currentDb,
                    peakFrequencyHz = uiState.peakFrequencyHz,
                    maxDb = uiState.maxDb
                )

                // 3. Alarm Counter & Elapsed Duration Card
                AlarmAndTimerCard(
                    alarmCount = uiState.alarmCount,
                    elapsedSeconds = uiState.elapsedSeconds,
                    isAlarmActive = uiState.isAlarmFlashing,
                    isMonitoring = uiState.isMonitoring
                )

                // 4. Threshold & Trigger Settings Card
                ThresholdControlsCard(
                    dbThreshold = uiState.dbThreshold,
                    khzThreshold = uiState.khzThreshold,
                    triggerMode = uiState.triggerMode,
                    onDbChanged = { viewModel.setDbThreshold(it) },
                    onDbAdjust = { viewModel.adjustDbThreshold(it) },
                    onKhzChanged = { viewModel.setKhzThreshold(it) },
                    onKhzAdjust = { viewModel.adjustKhzThreshold(it) },
                    onModeChanged = { viewModel.setTriggerMode(it) },
                    onTestBeep = { viewModel.testBeep() }
                )

                // 5. Action Buttons (Start/Stop, Reset)
                ActionControls(
                    isMonitoring = uiState.isMonitoring,
                    onToggleMonitoring = {
                        if (!uiState.permissionGranted) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            viewModel.toggleMonitoring()
                        }
                    },
                    onResetStats = { viewModel.resetStatistics() }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HeaderBar(
    isMonitoring: Boolean,
    permissionGranted: Boolean,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Elegant Dark rounded icon badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = stringResource(R.string.title_sound_monitor),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.subtitle_sound_monitor),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Theme Toggle Button
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Status Badge
            val statusBg = if (!permissionGranted) {
                ElegantCoral.copy(alpha = 0.2f)
            } else if (isMonitoring) {
                ElegantGreen.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }

            val statusColor = if (!permissionGranted) {
                ElegantCoral
            } else if (isMonitoring) {
                ElegantGreen
            } else {
                MaterialTheme.colorScheme.outline
            }

            val statusBorder = if (!permissionGranted) {
                ElegantCoral.copy(alpha = 0.4f)
            } else if (isMonitoring) {
                ElegantGreen.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            }

            val statusText = if (!permissionGranted) {
                "عدم دسترسی"
            } else if (isMonitoring) {
                "در حال پایش"
            } else {
                "متوقف"
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(statusBg)
                    .border(1.dp, statusBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("status_indicator_badge"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("permission_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantCoral.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MicOff,
                contentDescription = null,
                tint = ElegantCoral,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.permission_mic_required),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = ElegantTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElegantCoral,
                    contentColor = ElegantOnCoral
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.testTag("grant_permission_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = ElegantOnCoral,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.grant_permission),
                    color = ElegantOnCoral,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
