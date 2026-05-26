package com.denseframe.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denseframe.designsystem.DenseFrameCard
import com.denseframe.designsystem.DenseFrameColors
import com.denseframe.designsystem.DenseFramePrimaryButton
import com.denseframe.designsystem.DenseFrameScreen
import com.denseframe.designsystem.DenseFrameTheme
import com.denseframe.designsystem.MetricBar

@Composable
fun ProcessingScreen(
    onOpenViewer: () -> Unit,
    onBack: () -> Unit,
) {
    DenseFrameScreen(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text("Local processing", style = MaterialTheme.typography.headlineMedium)
            DenseFrameCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Point cloud MVP shell", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Processing is represented as resumable UI only. No reconstruction logic runs in this shell.",
                        color = DenseFrameColors.TextSecondary,
                    )
                    MetricBar("Validation", 1f, DenseFrameColors.Success)
                    MetricBar("Keyframes", 0.62f, DenseFrameColors.Data)
                    MetricBar("Point cloud", 0.36f, DenseFrameColors.Primary)
                }
            }
            DenseFramePrimaryButton("Open Viewer Shell", onClick = onOpenViewer, modifier = Modifier.fillMaxWidth())
            DenseFramePrimaryButton("Back to Review", onClick = onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun ProcessingPreview() {
    DenseFrameTheme {
        ProcessingScreen(onOpenViewer = {}, onBack = {})
    }
}
