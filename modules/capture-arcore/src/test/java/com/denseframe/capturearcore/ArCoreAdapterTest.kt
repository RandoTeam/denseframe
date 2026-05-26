package com.denseframe.capturearcore

import com.denseframe.captureapi.TrackingState
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import com.google.ar.core.ArCoreApk

class ArCoreAdapterTest {
    @Test
    fun availabilityStatusMapping() {
        val checker = ArCoreAvailabilityChecker()

        assertEquals(ArCoreAvailabilityStatus.SupportedInstalled, checker.map(ArCoreApk.Availability.SUPPORTED_INSTALLED))
        assertEquals(ArCoreAvailabilityStatus.SupportedApkTooOld, checker.map(ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD))
        assertEquals(ArCoreAvailabilityStatus.SupportedNotInstalled, checker.map(ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED))
        assertEquals(ArCoreAvailabilityStatus.UnsupportedDevice, checker.map(ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE))
    }

    @Test
    fun depthSupportSelectionPrefersRawDepthOnly() {
        val result = ArCoreDepthModeSelector.select(FakeDepthSupport(raw = true, automatic = true))

        assertEquals(ArCoreDepthSupport.RawDepthOnly, result.depthSupport)
        assertEquals("RAW_DEPTH_ONLY", result.selectedDepthModeName)
    }

    @Test
    fun depthSupportSelectionAllowsAutomaticFallback() {
        val result = ArCoreDepthModeSelector.select(FakeDepthSupport(raw = false, automatic = true))

        assertEquals(ArCoreDepthSupport.AutomaticFallback, result.depthSupport)
        assertEquals("AUTOMATIC", result.selectedDepthModeName)
    }

    @Test
    fun depthSupportSelectionRejectsNoDepthSupport() {
        val result = ArCoreDepthModeSelector.select(FakeDepthSupport(raw = false, automatic = false))

        assertEquals(ArCoreDepthSupport.Unsupported, result.depthSupport)
        assertEquals(null, result.selectedDepthModeName)
    }

    @Test
    fun exceptionMapping() {
        val error = ArCoreExceptionMapper.map(IllegalStateException("bad lifecycle"))

        assertEquals("illegal_state", error.code)
        assertTrue(error.recoverable)
    }

    @Test
    fun trackingStateMapping() {
        assertEquals(TrackingState.TRACKING, ArCoreTrackingMapper.map("TRACKING"))
        assertEquals(TrackingState.LIMITED, ArCoreTrackingMapper.map("PAUSED"))
        assertEquals(TrackingState.LOST, ArCoreTrackingMapper.map("STOPPED"))
        assertEquals(TrackingState.UNKNOWN, ArCoreTrackingMapper.map("unexpected"))
    }

    @Test
    fun poseMatrixPreservesColumnMajorOrder() {
        val values = FloatArray(16) { it + 0.5f }
        val pose = ArCorePoseMapper.map(FakePose(values))

        assertEquals(values.toList(), pose.valuesColumnMajor)
    }

    @Test
    fun intrinsicsConversionCopiesValuesExactly() {
        val intrinsics = ArCoreIntrinsicsMapper.map(
            FakeIntrinsics(
                dimensions = intArrayOf(640, 480),
                focal = floatArrayOf(500f, 501f),
                principal = floatArrayOf(320f, 240f),
            ),
        )

        assertEquals(640, intrinsics.width)
        assertEquals(480, intrinsics.height)
        assertEquals(500f, intrinsics.fx)
        assertEquals(501f, intrinsics.fy)
        assertEquals(320f, intrinsics.cx)
        assertEquals(240f, intrinsics.cy)
    }

    @Test
    fun depthPackingContiguousRows() {
        val image = FakeImage(
            width = 2,
            height = 2,
            timestampNanos = 10,
            plane = FakePlane(byteArrayOf(1, 0, 2, 0, 3, 0, 4, 0), rowStride = 4, pixelStride = 2),
        )

        assertArrayEquals(byteArrayOf(1, 0, 2, 0, 3, 0, 4, 0), ArCoreImagePlanePacker.packDepthU16(image))
    }

    @Test
    fun depthPackingRemovesRowPadding() {
        val image = FakeImage(
            width = 2,
            height = 2,
            timestampNanos = 10,
            plane = FakePlane(byteArrayOf(1, 0, 2, 0, 99, 99, 3, 0, 4, 0, 88, 88), rowStride = 6, pixelStride = 2),
        )

        assertArrayEquals(byteArrayOf(1, 0, 2, 0, 3, 0, 4, 0), ArCoreImagePlanePacker.packDepthU16(image))
    }

    @Test(expected = ArCoreImagePackingException::class)
    fun depthPackingRejectsUnexpectedPixelStride() {
        ArCoreImagePlanePacker.packDepthU16(
            FakeImage(
                width = 1,
                height = 1,
                timestampNanos = 10,
                plane = FakePlane(byteArrayOf(1, 0), rowStride = 2, pixelStride = 1),
            ),
        )
    }

    @Test
    fun confidencePackingRemovesRowPadding() {
        val image = FakeImage(
            width = 2,
            height = 2,
            timestampNanos = 10,
            plane = FakePlane(byteArrayOf(7, 8, 99, 9, 10, 88), rowStride = 3, pixelStride = 1),
        )

        assertArrayEquals(byteArrayOf(7, 8, 9, 10), ArCoreImagePlanePacker.packConfidenceU8(image))
    }

    @Test
    fun depthFreshnessClassifiesNewAndReprojectedDepth() {
        val tracker = ArCoreDepthFreshnessTracker()

        assertEquals(ArCoreDepthFreshness.NEW_DEPTH, tracker.classify(frameTimestampNanos = 100, rawDepthTimestampNanos = 100))
        assertEquals(ArCoreDepthFreshness.REPROJECTED_DEPTH, tracker.classify(frameTimestampNanos = 101, rawDepthTimestampNanos = 100))
        assertEquals(ArCoreDepthFreshness.REPROJECTED_DEPTH, tracker.classify(frameTimestampNanos = 102, rawDepthTimestampNanos = 99))
    }

    @Test
    fun framePacketReadyClosesImagesAndMapsPayloads() {
        val depth = FakeImage(2, 2, 100, FakePlane(byteArrayOf(1, 0, 0, 0, 3, 0, 4, 0), 4, 2))
        val confidence = FakeImage(2, 2, 100, FakePlane(byteArrayOf(255.toByte(), 0, 128.toByte(), 64), 2, 1))
        val source = ArCoreFramePacketSource(receivedNanos = { 111 })

        val result = source.acquire(FakeFrame(timestampNanos = 100, depth = depth, confidence = confidence))

        assertTrue(result is ArCoreFramePacketResult.FramePacketReady)
        val ready = result as ArCoreFramePacketResult.FramePacketReady
        assertEquals(100, ready.packet.timestamp.sensorNanos)
        assertEquals(111, ready.packet.timestamp.receivedNanos)
        assertEquals(2, ready.packet.depth?.width)
        assertEquals(2, ready.packet.confidence?.height)
        assertEquals(ArCoreDepthFreshness.NEW_DEPTH, ready.depthFreshness)
        assertTrue(depth.closed)
        assertTrue(confidence.closed)
    }

    @Test
    fun trackingLostDoesNotEmitPacket() {
        val source = ArCoreFramePacketSource()

        val result = source.acquire(FakeFrame(camera = FakeCamera(tracking = "PAUSED", failure = "INSUFFICIENT_LIGHT")))

        assertTrue(result is ArCoreFramePacketResult.TrackingUnavailable)
        val unavailable = result as ArCoreFramePacketResult.TrackingUnavailable
        assertEquals(TrackingState.LIMITED, unavailable.trackingState)
        assertEquals("INSUFFICIENT_LIGHT", unavailable.failureReason)
    }

    @Test
    fun resourcesCloseOnPackingException() {
        val depth = FakeImage(2, 2, 100, FakePlane(byteArrayOf(1, 0, 2, 0), 4, 1))
        val confidence = FakeImage(2, 2, 100, FakePlane(byteArrayOf(1, 2, 3, 4), 2, 1))
        val source = ArCoreFramePacketSource()

        val result = source.acquire(FakeFrame(timestampNanos = 100, depth = depth, confidence = confidence))

        assertTrue(result is ArCoreFramePacketResult.Fatal || result is ArCoreFramePacketResult.Rejected)
        assertTrue(depth.closed)
        assertTrue(confidence.closed)
    }

    @Test
    fun boundedQueueDropsDeterministically() {
        val queue = ArCoreBoundedFrameQueue<Int>(capacity = 1)

        assertTrue(queue.offer(1))
        assertFalse(queue.offer(2))
        assertEquals(1, queue.dropped)
        assertEquals(1, queue.poll())
    }

    private class FakePose(private val values: FloatArray) : ArCorePoseLike {
        override fun toMatrix(values: FloatArray, offset: Int) {
            this.values.copyInto(values, offset)
        }
    }

    private class FakeDepthSupport(
        private val raw: Boolean,
        private val automatic: Boolean,
    ) : ArCoreDepthModeSupport {
        override fun isRawDepthOnlySupported(): Boolean = raw
        override fun isAutomaticSupported(): Boolean = automatic
    }

    private class FakeIntrinsics(
        private val dimensions: IntArray = intArrayOf(2, 2),
        private val focal: FloatArray = floatArrayOf(100f, 101f),
        private val principal: FloatArray = floatArrayOf(1f, 1f),
    ) : ArCoreCameraIntrinsicsLike {
        override fun imageDimensions(): IntArray = dimensions
        override fun focalLength(): FloatArray = focal
        override fun principalPoint(): FloatArray = principal
    }

    private class FakeCamera(
        private val tracking: String = "TRACKING",
        private val failure: String = "NONE",
    ) : ArCoreCameraLike {
        override fun trackingStateName(): String = tracking
        override fun trackingFailureReasonName(): String = failure
        override fun pose(): ArCorePoseLike = FakePose(FloatArray(16) { if (it % 5 == 0) 1f else 0f })
        override fun imageIntrinsics(): ArCoreCameraIntrinsicsLike = FakeIntrinsics()
    }

    private class FakeFrame(
        override val timestampNanos: Long = 100,
        private val camera: ArCoreCameraLike = FakeCamera(),
        private val depth: ArCoreImageLike = FakeImage(2, 2, 100, FakePlane(byteArrayOf(1, 0, 2, 0, 3, 0, 4, 0), 4, 2)),
        private val confidence: ArCoreImageLike = FakeImage(2, 2, 100, FakePlane(byteArrayOf(255.toByte(), 255.toByte(), 128.toByte(), 128.toByte()), 2, 1)),
    ) : ArCoreFrameLike {
        override fun camera(): ArCoreCameraLike = camera
        override fun acquireRawDepthImage16Bits(): ArCoreImageLike = depth
        override fun acquireRawDepthConfidenceImage(): ArCoreImageLike = confidence
    }

    private class FakeImage(
        override val width: Int,
        override val height: Int,
        override val timestampNanos: Long,
        plane: ArCoreImagePlaneLike,
    ) : ArCoreImageLike {
        var closed = false
        override val planes: List<ArCoreImagePlaneLike> = listOf(plane)
        override fun close() {
            closed = true
        }
    }

    private class FakePlane(
        bytes: ByteArray,
        override val rowStride: Int,
        override val pixelStride: Int,
    ) : ArCoreImagePlaneLike {
        override val buffer: ByteBuffer = ByteBuffer.wrap(bytes)
    }
}
