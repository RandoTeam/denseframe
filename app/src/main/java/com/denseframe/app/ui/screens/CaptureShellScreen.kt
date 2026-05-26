package com.denseframe.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denseframe.app.capture.ArCoreCaptureUiState
import com.denseframe.designsystem.DenseFrameCard
import com.denseframe.designsystem.DenseFrameColors
import com.denseframe.designsystem.DenseFramePrimaryButton
import com.denseframe.designsystem.DenseFrameScreen
import com.denseframe.designsystem.DenseFrameTheme
import com.denseframe.designsystem.MetricBar
import com.denseframe.designsystem.StatusPill

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
    DenseFrameScreen(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DenseFrameColors.SurfaceRaised, RoundedCornerShape(24.dp)),
            ) {
                captureSurface()
                Text(
                    if (state.isCapturing) "ARCore capture surface active - camera preview rendering deferred" else "ARCore capture surface idle",
                    modifier = Modifier.align(Alignment.Center),
                    color = DenseFrameColors.TextTertiary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatusPill("ARCore", state.availability, statusColor(state.availability))
                    StatusPill("Permission", state.cameraPermission, statusColor(state.cameraPermission))
                    StatusPill("Depth", state.selectedDepthMode, statusColor(state.depthSupport))
                }
                DenseFrameCard(modifier = Modifier.fillMaxWidth().widthIn(max = 760.dp)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatusPill("Session", state.sessionState, statusColor(state.sessionState))
                            StatusPill("Tracking", state.trackingState, statusColor(state.trackingState))
                            StatusPill("Freshness", state.depthFreshness, statusColor(state.depthFreshness))
                        }
                        MetricBar("Accepted frames", normalizedCount(state.acceptedFrames), DenseFrameColors.Primary)
                        MetricBar("Rejected frames", normalizedCount(state.rejectedFrames), DenseFrameColors.Warning)
                        MetricBar("Dropped frames", normalizedCount(state.droppedFrames), DenseFrameColors.Error)
                        Text("Last: ${state.lastFrameResult}", color = DenseFrameColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                        Text("Tracking detail: ${state.trackingFailureReason}", color = DenseFrameColors.TextTertiary, style = MaterialTheme.typography.bodySmall)
                        Text("Project: ${state.projectId ?: "not created"}", color = DenseFrameColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                        Text("Path: ${state.projectPath ?: "none"}", color = DenseFrameColors.TextTertiary, style = MaterialTheme.typography.bodySmall)
                        state.storageWriteError?.let {
                            Text("Storage: $it", color = DenseFrameColors.Error, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DenseFramePrimaryButton("Mode", onClick = onBack, modifier = Modifier.weight(1f), enabled = !state.isCapturing)
                DenseFramePrimaryButton("Permission", onClick = onRequestPermission, modifier = Modifier.weight(1f), enabled = state.cameraPermission != "Granted")
                DenseFramePrimaryButton(
                    if (state.isCapturing) "Stop Capture" else "Start Capture",
                    onClick = { if (state.isCapturing) onStopCapture() else onStart() },
                    modifier = Modifier.weight(2f),
                    enabled = state.canStart || state.isCapturing,
                )
                DenseFramePrimaryButton("Review", onClick = onStop, modifier = Modifier.weight(1f), enabled = !state.isCapturing && state.projectId != null)
            }
        }
    }
}

private fun normalizedCount(count: Int): Float = (count.coerceAtMost(100) / 100f).coerceIn(0f, 1f)

private fun statusColor(value: String) = when {
    value.contains("TRACKING", ignoreCase = true) -> DenseFrameColors.Primary
    value.contains("Granted", ignoreCase = true) -> DenseFrameColors.Primary
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
