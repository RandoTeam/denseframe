package com.denseframe.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denseframe.designsystem.DenseFrameCard
import com.denseframe.designsystem.DenseFrameColors
import com.denseframe.designsystem.DenseFramePrimaryButton
import com.denseframe.designsystem.DenseFrameScreen
import com.denseframe.designsystem.DenseFrameTheme
import com.denseframe.designsystem.MetricBar
import com.denseframe.designsystem.StatusPill

@Composable
fun CaptureShellScreen(
    onStop: () -> Unit,
    onBack: () -> Unit,
) {
    DenseFrameScreen(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DenseFrameColors.SurfaceRaised, RoundedCornerShape(24.dp)),
            ) {
                Text(
                    "Camera preview shell - no scanner implementation",
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
                    StatusPill("Tracking", "Preview", DenseFrameColors.Warning)
                    StatusPill("Depth", "Not live", DenseFrameColors.Warning)
                }
                DenseFrameCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricBar("Confidence", 0.72f, DenseFrameColors.Data)
                        MetricBar("Coverage", 0.48f, DenseFrameColors.Primary)
                        MetricBar("Motion risk", 0.24f, DenseFrameColors.Warning)
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
                DenseFramePrimaryButton("Mode", onClick = onBack, modifier = Modifier.weight(1f))
                DenseFramePrimaryButton("Stop + Review", onClick = onStop, modifier = Modifier.weight(2f))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun CaptureShellPreview() {
    DenseFrameTheme {
        CaptureShellScreen(onStop = {}, onBack = {})
    }
}
