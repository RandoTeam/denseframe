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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
fun SceneViewerShellScreen(onBack: () -> Unit) {
    var showExport by remember { mutableStateOf(false) }

    DenseFrameScreen(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DenseFrameColors.SurfaceRaised, RoundedCornerShape(24.dp)),
            ) {
                Text(
                    "Point cloud viewer shell",
                    modifier = Modifier.align(Alignment.Center),
                    color = DenseFrameColors.TextSecondary,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatusPill("Artifact", "Sample UI", DenseFrameColors.Warning)
                StatusPill("Renderer", "Deferred", DenseFrameColors.Data)
            }

            DenseFrameCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Viewer controls", style = MaterialTheme.typography.titleMedium)
                    Text("Orbit, pan, zoom, reset, inspect, and export surfaces are defined here without Filament.", color = DenseFrameColors.TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DenseFramePrimaryButton("Gallery", onClick = onBack, modifier = Modifier.weight(1f))
                        DenseFramePrimaryButton("Export", onClick = { showExport = true }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    if (showExport) {
        ExportSheet(onDismiss = { showExport = false })
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ExportSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DenseFrameColors.Surface,
        contentColor = DenseFrameColors.TextPrimary,
    ) {
        Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Export placeholder", style = MaterialTheme.typography.titleLarge)
            Text(
                "DFRZ, PLY, and GLB exports are intentionally not implemented in the shell. Future exports must write temp files first, support cancellation, and report metrics.",
                color = DenseFrameColors.TextSecondary,
            )
            DenseFramePrimaryButton("Close", onClick = onDismiss, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun SceneViewerPreview() {
    DenseFrameTheme {
        SceneViewerShellScreen(onBack = {})
    }
}
