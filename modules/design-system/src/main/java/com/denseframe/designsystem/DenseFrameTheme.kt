package com.denseframe.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object DenseFrameColors {
    val Background = Color(0xFF090D12)
    val Surface = Color(0xFF121821)
    val SurfaceRaised = Color(0xFF1A222D)
    val Primary = Color(0xFF7BE7D4)
    val PrimaryDim = Color(0xFF244A45)
    val Secondary = Color(0xFF9FB5FF)
    val TextPrimary = Color(0xFFF4F7FB)
    val TextSecondary = Color(0xFFB5C0CC)
    val TextTertiary = Color(0xFF738091)
    val Success = Color(0xFF60D394)
    val Warning = Color(0xFFFFC857)
    val Error = Color(0xFFFF6B6B)
    val Data = Color(0xFF64D2FF)
    val Disabled = Color(0xFF46515F)
    val Outline = Color(0xFF2B3645)
}

private val DenseFrameDarkColorScheme: ColorScheme = darkColorScheme(
    primary = DenseFrameColors.Primary,
    onPrimary = Color(0xFF06211D),
    secondary = DenseFrameColors.Secondary,
    background = DenseFrameColors.Background,
    onBackground = DenseFrameColors.TextPrimary,
    surface = DenseFrameColors.Surface,
    onSurface = DenseFrameColors.TextPrimary,
    surfaceVariant = DenseFrameColors.SurfaceRaised,
    onSurfaceVariant = DenseFrameColors.TextSecondary,
    outline = DenseFrameColors.Outline,
    error = DenseFrameColors.Error,
    onError = Color(0xFF2B0909),
)

@Composable
fun DenseFrameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DenseFrameDarkColorScheme,
        typography = DenseFrameTypography,
        content = content,
    )
}
