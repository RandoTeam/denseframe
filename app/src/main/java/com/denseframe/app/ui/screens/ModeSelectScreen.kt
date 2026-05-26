package com.denseframe.app.ui.screens

import androidx.compose.foundation.clickable
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
import com.denseframe.designsystem.DenseFrameScreen
import com.denseframe.designsystem.DenseFrameTheme
import com.denseframe.designsystem.StatusPill

@Composable
fun ModeSelectScreen(
    onBack: () -> Unit,
    onModeSelected: () -> Unit,
) {
    DenseFrameScreen(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Choose scan mode", style = MaterialTheme.typography.headlineMedium)
                    Text("Guidance changes by target size and motion budget.", color = DenseFrameColors.TextSecondary)
                }
                Text("Back", color = DenseFrameColors.Primary, modifier = Modifier.clickable(onClick = onBack))
            }

            ModeCard("Object", "Walk around a small subject. Watch coverage and reflective surface warnings.", "Best for: desk objects", onModeSelected)
            ModeCard("Room", "Move slowly around the space. Keep storage and thermal status visible.", "Best for: interiors", onModeSelected)
            ModeCard("Quick scene", "Capture a fast spatial reference. Point cloud preview comes first.", "Best for: rough context", onModeSelected)
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    body: String,
    tag: String,
    onClick: () -> Unit,
) {
    DenseFrameCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                StatusPill("Mode", "Ready", DenseFrameColors.Success)
            }
            Text(body, color = DenseFrameColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Text(tag, color = DenseFrameColors.Data, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun ModeSelectPreview() {
    DenseFrameTheme {
        ModeSelectScreen(onBack = {}, onModeSelected = {})
    }
}
