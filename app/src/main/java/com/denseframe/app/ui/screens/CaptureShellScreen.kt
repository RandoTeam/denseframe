package com.denseframe.app.ui.screens

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denseframe.app.capture.ArCoreCaptureUiState
import com.denseframe.designsystem.DenseFrameCard
import com.denseframe.designsystem.DenseFrameColors
import com.denseframe.designsystem.DenseFramePrimaryButton
import com.denseframe.designsystem.DenseFrameScreen
import com.denseframe.designsystem.DenseFrameTheme

@Composable
fun CaptureShellScreen(
    state: ArCoreCaptureUiState,
    onRequestPermission: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onStopCapture: () -> Unit,
    onBack: () -> Unit,
    captureSurface: @Composable () -> Unit = {},
) {
    val hasPermission = state.cameraPermission == "Granted"
    DenseFrameScreen(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            CaptureSurfaceFrame(
                isCapturing = state.isCapturing,
                hasPermission = hasPermission,
                captureSurface = captureSurface,
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CaptureHeader(state = state)
                ReadinessPanel(
                    state = state,
                    hasPermission = hasPermission,
                    onRequestPermission = onRequestPermission,
                    onStart = onStart,
                    onStopCapture = onStopCapture,
                    onBack = onBack,
                )
                CaptureCounters(state = state)
                ProjectPanel(state = state, onReview = onStop)
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun CaptureSurfaceFrame(
    isCapturing: Boolean,
    hasPermission: Boolean,
    captureSurface: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(DenseFrameColors.SurfaceRaised),
    ) {
        if (isCapturing) {
            captureSurface()
        }
        if (!isCapturing) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = if (hasPermission) "Ready for ARCore depth capture" else "Camera access is required",
                    color = DenseFrameColors.TextSecondary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (hasPermission) {
                        "Start capture to open the AR session and write DFR frames."
                    } else {
                        "Grant camera permission before starting a local scan."
                    },
                    color = DenseFrameColors.TextTertiary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun CaptureHeader(state: ArCoreCaptureUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CompactStatusCard(
            label = "ARCore",
            value = state.availability,
            color = statusColor(state.availability),
            modifier = Modifier.weight(1f),
        )
        CompactStatusCard(
            label = "Camera",
            value = state.cameraPermission,
            color = statusColor(state.cameraPermission),
            modifier = Modifier.weight(1f),
        )
        CompactStatusCard(
            label = "Depth",
            value = if (state.selectedDepthMode == "None") state.depthSupport else state.selectedDepthMode,
            color = statusColor(state.depthSupport),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ReadinessPanel(
    state: ArCoreCaptureUiState,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onStart: () -> Unit,
    onStopCapture: () -> Unit,
    onBack: () -> Unit,
) {
    DenseFrameCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = if (state.isCapturing) "Capturing raw depth" else "Capture setup",
                color = DenseFrameColors.TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = captureGuidance(state, hasPermission),
                color = DenseFrameColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                DenseFramePrimaryButton(
                    text = "Mode",
                    onClick = onBack,
                    modifier = Modifier.weight(0.8f),
                    enabled = !state.isCapturing,
                )
                DenseFramePrimaryButton(
                    text = if (hasPermission) {
                        if (state.isCapturing) "Stop" else "Start"
                    } else {
                        "Allow camera"
                    },
                    onClick = {
                        when {
                            !hasPermission -> onRequestPermission()
                            state.isCapturing -> onStopCapture()
                            else -> onStart()
                        }
                    },
                    modifier = Modifier.weight(1.6f),
                    enabled = !state.isCapturing || hasPermission,
                )
            }
        }
    }
}

@Composable
private fun CaptureCounters(state: ArCoreCaptureUiState) {
    DenseFrameCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CompactStatusCard("Session", state.sessionState, statusColor(state.sessionState), Modifier.weight(1f))
                CompactStatusCard("Tracking", state.trackingState, statusColor(state.trackingState), Modifier.weight(1f))
                CompactStatusCard("Depth", state.depthFreshness, statusColor(state.depthFreshness), Modifier.weight(1f))
            }
            CounterRow("Accepted", state.acceptedFrames, DenseFrameColors.Primary)
            CounterRow("Rejected", state.rejectedFrames, DenseFrameColors.Warning)
            CounterRow("Dropped", state.droppedFrames, DenseFrameColors.Error)
            CounterRow("No depth", state.noDepthFrames, DenseFrameColors.TextTertiary)
            DetailLine("Last frame", state.lastFrameResult)
            DetailLine("Tracking detail", state.trackingFailureReason)
            state.storageWriteError?.let {
                DetailLine("Storage", it, DenseFrameColors.Error)
            }
        }
    }
}

@Composable
private fun ProjectPanel(
    state: ArCoreCaptureUiState,
    onReview: () -> Unit,
) {
    DenseFrameCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Raw project",
                color = DenseFrameColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            DetailLine("Project", state.projectId ?: "Not created")
            DetailLine("Path", state.projectPath ?: "Created after Start")
            DenseFramePrimaryButton(
                text = "Review saved capture",
                onClick = onReview,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isCapturing && state.projectId != null,
            )
        }
    }
}

@Composable
private fun CompactStatusCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, color.copy(alpha = 0.38f), RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, RoundedCornerShape(999.dp)),
            )
            Text(
                text = label,
                color = DenseFrameColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = value,
            color = DenseFrameColors.TextPrimary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CounterRow(label: String, value: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, RoundedCornerShape(999.dp)),
            )
            Text(text = label, color = DenseFrameColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = value.toString(),
            color = DenseFrameColors.TextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun DetailLine(label: String, value: String, color: Color = DenseFrameColors.TextSecondary) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(text = label, color = DenseFrameColors.TextTertiary, style = MaterialTheme.typography.labelSmall)
        Text(
            text = value,
            color = color,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun captureGuidance(state: ArCoreCaptureUiState, hasPermission: Boolean): String = when {
    !hasPermission -> "DenseFrame needs camera access to create an ARCore session. Nothing is uploaded."
    state.isCapturing -> "Move slowly around a textured object. Avoid reflective surfaces and keep the phone steady."
    state.availability.contains("Unsupported", ignoreCase = true) -> "This device does not report ARCore support for capture."
    else -> "Start creates a local DFR project, checks raw-depth support, and writes accepted frames on device."
}

private fun statusColor(value: String) = when {
    value.contains("TRACKING", ignoreCase = true) -> DenseFrameColors.Primary
    value.contains("Granted", ignoreCase = true) -> DenseFrameColors.Primary
    value.contains("Supported", ignoreCase = true) -> DenseFrameColors.Primary
    value.contains("RAW_DEPTH", ignoreCase = true) -> DenseFrameColors.Primary
    value.contains("NEW_DEPTH", ignoreCase = true) -> DenseFrameColors.Primary
    value.contains("FAILED", ignoreCase = true) -> DenseFrameColors.Error
    value.contains("Unsupported", ignoreCase = true) -> DenseFrameColors.Error
    value.contains("Denied", ignoreCase = true) -> DenseFrameColors.Error
    else -> DenseFrameColors.Warning
}

@Preview(showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun CaptureShellPreview() {
    DenseFrameTheme {
        CaptureShellScreen(
            state = ArCoreCaptureUiState(),
            onRequestPermission = {},
            onStart = {},
            onStop = {},
            onStopCapture = {},
            onBack = {},
        )
    }
}
