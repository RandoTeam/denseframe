package com.denseframe.capturearcore

class ArCoreImagePackingException(message: String) : IllegalArgumentException(message)

object ArCoreImagePlanePacker {
    fun packDepthU16(image: ArCoreImageLike): ByteArray {
        val plane = image.singlePlane()
        if (plane.pixelStride != 2) {
            throw ArCoreImagePackingException("Raw depth pixel stride must be 2, got ${plane.pixelStride}")
        }
        val expectedRowBytes = image.width * 2
        if (plane.rowStride < expectedRowBytes) {
            throw ArCoreImagePackingException("Raw depth row stride ${plane.rowStride} is smaller than row bytes $expectedRowBytes")
        }
        return packRows(image.width, image.height, bytesPerPixel = 2, plane = plane)
    }

    fun packConfidenceU8(image: ArCoreImageLike): ByteArray {
        val plane = image.singlePlane()
        if (plane.pixelStride != 1) {
            throw ArCoreImagePackingException("Raw confidence pixel stride must be 1, got ${plane.pixelStride}")
        }
        if (plane.rowStride < image.width) {
            throw ArCoreImagePackingException("Raw confidence row stride ${plane.rowStride} is smaller than width ${image.width}")
        }
        return packRows(image.width, image.height, bytesPerPixel = 1, plane = plane)
    }

    private fun ArCoreImageLike.singlePlane(): ArCoreImagePlaneLike {
        require(width > 0 && height > 0) { "Image dimensions must be positive" }
        require(planes.size == 1) { "ARCore raw image must have exactly one plane" }
        return planes[0]
    }

    private fun packRows(
        width: Int,
        height: Int,
        bytesPerPixel: Int,
        plane: ArCoreImagePlaneLike,
    ): ByteArray {
        val source = plane.buffer.duplicate()
        val output = ByteArray(width * height * bytesPerPixel)
        var target = 0
        for (row in 0 until height) {
            val rowStart = row * plane.rowStride
            for (column in 0 until width) {
                val sourceIndex = rowStart + column * plane.pixelStride
                for (byteIndex in 0 until bytesPerPixel) {
                    output[target++] = source.get(sourceIndex + byteIndex)
                }
            }
        }
        return output
    }
}
