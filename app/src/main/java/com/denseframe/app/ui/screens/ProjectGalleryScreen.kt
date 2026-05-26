package com.denseframe.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.denseframe.designsystem.StatusPill

@Composable
fun ProjectGalleryScreen(
    onNewScan: () -> Unit,
    onOpenSampleProject: () -> Unit,
) {
    DenseFrameScreen(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("DenseFrame", style = MaterialTheme.typography.displaySmall)
                    Text(
                        "Local scan projects",
                        color = DenseFrameColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                StatusPill("Storage", "Ready", DenseFrameColors.Success)
            }

            DenseFramePrimaryButton("New Scan", onClick = onNewScan, modifier = Modifier.fillMaxWidth())

            DenseFrameCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("No real scans yet", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "The first project will appear here after a raw DFR capture is saved locally.",
                        color = DenseFrameColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatusPill("Cloud", "Off", DenseFrameColors.Primary)
                        StatusPill("Runtime", "Local", DenseFrameColors.Data)
                    }
                }
            }

            DenseFrameCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("UI preview sample", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "This is sample shell content only. It is not scanner output.",
                        color = DenseFrameColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(2.dp))
                    DenseFramePrimaryButton("Open Viewer Shell", onClick = onOpenSampleProject, modifier = Modifier.width(220.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun ProjectGalleryPreview() {
    DenseFrameTheme {
        ProjectGalleryScreen(onNewScan = {}, onOpenSampleProject = {})
    }
}
