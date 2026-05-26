package com.denseframe.app.ui

enum class ShellDestination {
    Gallery,
    ModeSelect,
    Capture,
    SaveReview,
    Processing,
    Viewer,
}

fun ShellDestination.next(): ShellDestination = when (this) {
    ShellDestination.Gallery -> ShellDestination.ModeSelect
    ShellDestination.ModeSelect -> ShellDestination.Capture
    ShellDestination.Capture -> ShellDestination.SaveReview
    ShellDestination.SaveReview -> ShellDestination.Processing
    ShellDestination.Processing -> ShellDestination.Viewer
    ShellDestination.Viewer -> ShellDestination.Gallery
}
