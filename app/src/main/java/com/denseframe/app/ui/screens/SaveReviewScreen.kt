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
import com.denseframe.designsystem.DenseFrameCard
import com.denseframe.designsystem.DenseFrameColors
import com.denseframe.designsystem.DenseFramePrimaryButton
import com.denseframe.designsystem.DenseFrameScreen
import com.denseframe.designsystem.DenseFrameTheme
import com.denseframe.designsystem.StatusPill

@Composable
fun SaveReviewScreen(
    onProcess: () -> Unit,
    onBack: () -> Unit,
) {
    DenseFrameScreen(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text("Save review", style = MaterialTheme.typography.headlineMedium)
            DenseFrameCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Raw project shell", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "DFR write path is not implemented yet. This screen defines the review surface for frame counts, warnings, storage size, and restart status.",
                        color = DenseFrameColors.TextSecondary,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatusPill("Frames", "Sample", DenseFrameColors.Data)
                        StatusPill("Checksum", "Pending", DenseFrameColors.Warning)
                    }
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
        SaveReviewScreen(onProcess = {}, onBack = {})
    }
}
