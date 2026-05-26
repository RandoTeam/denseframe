package com.denseframe.captureapi

import org.junit.Assert.assertEquals
import org.junit.Test

class FramePacketModelTest {
    @Test
    fun poseConventionIsExplicit() {
        assertEquals("T_world_camera_column_major", PoseMatrix4x4.CONVENTION)
        PoseMatrix4x4(
            listOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f,
            ),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun poseRejectsWrongElementCount() {
        PoseMatrix4x4(listOf(1f, 2f))
    }

    @Test(expected = IllegalArgumentException::class)
    fun depthRejectsWrongPayloadSize() {
        DepthFrame(width = 2, height = 2, depthU16 = byteArrayOf(1, 2), metersPerUnit = 0.001f)
    }
}
