package com.denseframe.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denseframe.app.capture.ArCoreCaptureUiState
import com.denseframe.designsystem.DenseFrameCard
import com.denseframe.designsystem.DenseFrameColors
import com.denseframe.designsystem.DenseFramePrimaryButton
import com.denseframe.designsystem.DenseFrameScreen
import com.denseframe.designsystem.DenseFrameTheme
import com.denseframe.designsystem.StatusPill

@Composable
fun SaveReviewScreen(
    captureState: ArCoreCaptureUiState,
    onProcess: () -> Unit,
    onBack: () -> Unit,
) {
    DenseFrameScreen(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text("Save review", style = MaterialTheme.typography.headlineMedium)
            DenseFrameCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Raw project", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "DFR capture writes are saved locally when ARCore produces accepted tracked raw-depth frames. Reconstruction is not implemented yet.",
                        color = DenseFrameColors.TextSecondary,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatusPill("Accepted", captureState.acceptedFrames.toString(), DenseFrameColors.Data)
                        StatusPill("Rejected", captureState.rejectedFrames.toString(), DenseFrameColors.Warning)
                        StatusPill("Dropped", captureState.droppedFrames.toString(), DenseFrameColors.Error)
                    }
                    Text("Project: ${captureState.projectId ?: "none"}", color = DenseFrameColors.TextSecondary)
                    Text("Validation: ${captureState.lastFrameResult}", color = DenseFrameColors.TextTertiary)
                }
            }
            DenseFramePrimaryButton("Start Local Processing", onClick = onProcess, modifier = Modifier.fillMaxWidth())
            DenseFramePrimaryButton("Back to Capture", onClick = onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun SaveReviewPreview() {
    DenseFrameTheme {
        SaveReviewScreen(captureState = ArCoreCaptureUiState(), onProcess = {}, onBack = {})
    }
}
