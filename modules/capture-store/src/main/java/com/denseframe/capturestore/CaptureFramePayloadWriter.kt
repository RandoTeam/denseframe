package com.denseframe.capturestore

import com.denseframe.captureapi.FramePacket
import com.denseframe.projectstore.FramePayloadSources
import com.denseframe.projectstore.PayloadSource
import java.io.ByteArrayInputStream

class CaptureFramePayloadWriter {
    fun toPayloadSources(
        packet: FramePacket,
        policy: CaptureToDfrWritePolicy,
    ): FramePayloadSources = FramePayloadSources(
        colorYuv = null,
        depthU16 = packet.depth?.let { depth ->
            PayloadSource("depth_u16.bin") { ByteArrayInputStream(depth.depthU16) }
        },
        confidenceU8 = packet.confidence?.let { confidence ->
            PayloadSource("confidence_u8.bin") { ByteArrayInputStream(confidence.confidenceU8) }
        },
        colorFormat = packet.color?.format,
        depthFormat = packet.depth?.let { policy.depthPayloadFormat.storageName },
        confidenceFormat = packet.confidence?.let { policy.confidencePayloadFormat.storageName },
    )
}
