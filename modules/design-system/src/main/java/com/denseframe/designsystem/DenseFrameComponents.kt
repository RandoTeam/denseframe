package com.denseframe.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DenseFrameScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .background(DenseFrameColors.Background)
            .padding(20.dp),
    ) {
        content()
    }
}

@Composable
fun DenseFrameCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = DenseFrameColors.Surface,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, DenseFrameColors.Outline),
        content = content,
    )
}

@Composable
fun DenseFramePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DenseFrameColors.Primary,
            contentColor = Color(0xFF06211D),
            disabledContainerColor = DenseFrameColors.Disabled,
            disabledContentColor = DenseFrameColors.TextTertiary,
        ),
        contentPadding = PaddingValues(horizontal = 18.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun StatusPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .height(8.dp)
                .background(color, RoundedCornerShape(999.dp)),
        )
        Text(
            text = label,
            color = DenseFrameColors.TextSecondary,
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = value,
            color = DenseFrameColors.TextPrimary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun MetricBar(
    label: String,
    value: Float,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, color = DenseFrameColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
            Text(text = "${(value * 100).toInt()}%", color = DenseFrameColors.TextPrimary, style = MaterialTheme.typography.labelMedium)
        }
        LinearProgressIndicator(
            progress = { value.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = DenseFrameColors.SurfaceRaised,
        )
    }
}
