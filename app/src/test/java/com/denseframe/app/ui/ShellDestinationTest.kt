package com.denseframe.app.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class ShellDestinationTest {
    @Test
    fun nextFollowsMvpFlow() {
        assertEquals(ShellDestination.ModeSelect, ShellDestination.Gallery.next())
        assertEquals(ShellDestination.Capture, ShellDestination.ModeSelect.next())
        assertEquals(ShellDestination.SaveReview, ShellDestination.Capture.next())
        assertEquals(ShellDestination.Processing, ShellDestination.SaveReview.next())
        assertEquals(ShellDestination.Viewer, ShellDestination.Processing.next())
    }
}
