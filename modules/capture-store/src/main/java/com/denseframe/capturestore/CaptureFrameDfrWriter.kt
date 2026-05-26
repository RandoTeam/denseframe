package com.denseframe.capturestore

import com.denseframe.captureapi.FramePacket
import com.denseframe.projectstore.AppendFrameResult
import com.denseframe.projectstore.DfrProjectReader
import com.denseframe.projectstore.DfrProjectWriter
import java.nio.file.Path

class CaptureFrameDfrWriter(
    private val mapper: CaptureFrameDfrMapper = CaptureFrameDfrMapper(),
    private val projectReader: DfrProjectReader = DfrProjectReader(),
    private val projectWriter: DfrProjectWriter = DfrProjectWriter(),
) {
    fun appendFrame(
        projectDir: Path,
        packet: FramePacket,
        policy: CaptureToDfrWritePolicy = CaptureToDfrWritePolicy(),
    ): CaptureFrameWriteResult {
        val previousTimestamp = if (policy.strictMonotonicTimestamps) {
            projectReader.readManifest(projectDir).frames.maxOfOrNull { it.timestampNanos }
        } else {
            null
        }
        val validation = mapper.validate(packet, policy, previousTimestamp)
        if (validation is CaptureFrameValidationResult.Rejected) {
            return CaptureFrameWriteResult.Rejected(validation.reasons)
        }
        return when (val result = projectWriter.appendFrame(projectDir, mapper.toFrameWriteRequest(packet, policy))) {
            is AppendFrameResult.Appended -> CaptureFrameWriteResult.Written(result.frameManifest)
            is AppendFrameResult.Failed -> CaptureFrameWriteResult.Failed(result.reason, result.cause)
        }
    }
}
