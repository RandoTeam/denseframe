package com.denseframe.capturearcore

import android.media.Image
import com.google.ar.core.Camera
import com.google.ar.core.CameraIntrinsics
import com.google.ar.core.Frame
import com.google.ar.core.Pose
import java.nio.ByteBuffer

interface ArCoreFrameLike {
    val timestampNanos: Long
    fun camera(): ArCoreCameraLike
    fun acquireRawDepthImage16Bits(): ArCoreImageLike
    fun acquireRawDepthConfidenceImage(): ArCoreImageLike
}

interface ArCoreCameraLike {
    fun trackingStateName(): String
    fun trackingFailureReasonName(): String
    fun pose(): ArCorePoseLike
    fun imageIntrinsics(): ArCoreCameraIntrinsicsLike
}

interface ArCorePoseLike {
    fun toMatrix(values: FloatArray, offset: Int)
}

interface ArCoreCameraIntrinsicsLike {
    fun imageDimensions(): IntArray
    fun focalLength(): FloatArray
    fun principalPoint(): FloatArray
}

interface ArCoreImageLike : AutoCloseable {
    val width: Int
    val height: Int
    val timestampNanos: Long
    val planes: List<ArCoreImagePlaneLike>
}

interface ArCoreImagePlaneLike {
    val buffer: ByteBuffer
    val rowStride: Int
    val pixelStride: Int
}

class RealArCoreFrame(private val frame: Frame) : ArCoreFrameLike {
    override val timestampNanos: Long get() = frame.timestamp
    override fun camera(): ArCoreCameraLike = RealArCoreCamera(frame.camera)
    override fun acquireRawDepthImage16Bits(): ArCoreImageLike = RealArCoreImage(frame.acquireRawDepthImage16Bits())
    override fun acquireRawDepthConfidenceImage(): ArCoreImageLike = RealArCoreImage(frame.acquireRawDepthConfidenceImage())
}

class RealArCoreCamera(private val camera: Camera) : ArCoreCameraLike {
    override fun trackingStateName(): String = camera.trackingState.name
    override fun trackingFailureReasonName(): String = camera.trackingFailureReason.name
    override fun pose(): ArCorePoseLike = RealArCorePose(camera.pose)
    override fun imageIntrinsics(): ArCoreCameraIntrinsicsLike = RealArCoreCameraIntrinsics(camera.imageIntrinsics)
}

class RealArCorePose(private val pose: Pose) : ArCorePoseLike {
    override fun toMatrix(values: FloatArray, offset: Int) {
        pose.toMatrix(values, offset)
    }
}

class RealArCoreCameraIntrinsics(private val intrinsics: CameraIntrinsics) : ArCoreCameraIntrinsicsLike {
    override fun imageDimensions(): IntArray = intrinsics.imageDimensions
    override fun focalLength(): FloatArray = intrinsics.focalLength
    override fun principalPoint(): FloatArray = intrinsics.principalPoint
}

class RealArCoreImage(private val image: Image) : ArCoreImageLike {
    override val width: Int get() = image.width
    override val height: Int get() = image.height
    override val timestampNanos: Long get() = image.timestamp
    override val planes: List<ArCoreImagePlaneLike> get() = image.planes.map { RealArCoreImagePlane(it) }
    override fun close() {
        image.close()
    }
}

class RealArCoreImagePlane(private val plane: Image.Plane) : ArCoreImagePlaneLike {
    override val buffer: ByteBuffer get() = plane.buffer
    override val rowStride: Int get() = plane.rowStride
    override val pixelStride: Int get() = plane.pixelStride
}
