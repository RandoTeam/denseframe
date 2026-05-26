package com.denseframe.projectstore

internal object DfrJson {
    fun manifestToJson(manifest: ProjectManifest): String = buildString {
        appendObject {
            field("format", manifest.format)
            field("schemaVersion", manifest.schemaVersion.value)
            field("projectId", manifest.projectId.value)
            field("createdAtEpochMillis", manifest.createdAtEpochMillis)
            field("updatedAtEpochMillis", manifest.updatedAtEpochMillis)
            fieldObject("device") {
                field("manufacturer", manifest.device.manufacturer)
                field("model", manifest.device.model)
                field("osVersion", manifest.device.osVersion)
            }
            fieldObject("capture") {
                field("mode", manifest.capture.mode)
                field("coordinateConvention", manifest.capture.coordinateConvention)
            }
            fieldObject("reconstructionDefaults") {
                field("minDepthMeters", manifest.reconstructionDefaults.minDepthMeters)
                field("maxDepthMeters", manifest.reconstructionDefaults.maxDepthMeters)
                field("minConfidence", manifest.reconstructionDefaults.minConfidence)
            }
            fieldArray("frames") {
                manifest.frames.forEachIndexed { index, frame ->
                    if (index > 0) comma()
                    append(frameToJson(frame))
                }
            }
        }
    }

    fun frameToJson(frame: FrameManifest): String = buildString {
        appendObject {
            field("frameId", frame.frameId)
            field("timestampNanos", frame.timestampNanos)
            field("trackingState", frame.trackingState)
            fieldObject("cameraIntrinsics") {
                field("width", frame.cameraIntrinsics.width)
                field("height", frame.cameraIntrinsics.height)
                field("fx", frame.cameraIntrinsics.fx)
                field("fy", frame.cameraIntrinsics.fy)
                field("cx", frame.cameraIntrinsics.cx)
                field("cy", frame.cameraIntrinsics.cy)
            }
            fieldArray("poseMatrix") {
                frame.pose.values.forEachIndexed { index, value ->
                    if (index > 0) comma()
                    appendNumber(value)
                }
            }
            fieldObject("quality") {
                field("depthConfidence", frame.quality.depthConfidence)
                field("coverage", frame.quality.coverage)
                field("motionRisk", frame.quality.motionRisk)
                field("accepted", frame.quality.accepted)
                field("dropReason", frame.quality.dropReason)
            }
            fieldObject("payloads") {
                field("colorYuv", frame.payloads.colorYuv)
                field("depthU16", frame.payloads.depthU16)
                field("confidenceU8", frame.payloads.confidenceU8)
            }
            fieldArray("checksums") {
                frame.checksums.forEachIndexed { index, checksum ->
                    if (index > 0) comma()
                    appendObject {
                        field("path", checksum.path)
                        field("algorithm", checksum.algorithm)
                        field("value", checksum.value)
                    }
                }
            }
        }
    }

    fun checksumFileToText(checksums: List<Checksum>): String =
        checksums.joinToString(separator = "\n", postfix = "\n") { "${it.algorithm} ${it.value} ${it.path}" }

    fun manifestFromJson(json: String): ProjectManifest {
        val root = JsonParser(json).parseObject()
        val frames = root.list("frames").map { frameFromMap(it.asObject()) }
        return ProjectManifest(
            format = root.string("format"),
            schemaVersion = DfrSchemaVersion(root.int("schemaVersion")),
            projectId = ProjectId(root.string("projectId")),
            createdAtEpochMillis = root.long("createdAtEpochMillis"),
            updatedAtEpochMillis = root.long("updatedAtEpochMillis"),
            device = root.obj("device").let {
                DeviceInfo(
                    manufacturer = it.string("manufacturer"),
                    model = it.string("model"),
                    osVersion = it.string("osVersion"),
                )
            },
            capture = root.obj("capture").let {
                CaptureInfo(
                    mode = it.string("mode"),
                    coordinateConvention = it.string("coordinateConvention"),
                )
            },
            reconstructionDefaults = root.obj("reconstructionDefaults").let {
                ReconstructionDefaults(
                    minDepthMeters = it.float("minDepthMeters"),
                    maxDepthMeters = it.float("maxDepthMeters"),
                    minConfidence = it.int("minConfidence"),
                )
            },
            frames = frames,
        )
    }

    fun frameFromJson(json: String): FrameManifest = frameFromMap(JsonParser(json).parseObject())

    private fun frameFromMap(root: Map<String, Any?>): FrameManifest {
        val intrinsics = root.obj("cameraIntrinsics")
        val quality = root.obj("quality")
        val payloads = root.obj("payloads")
        val checksums = root.list("checksums").map {
            val checksum = it.asObject()
            Checksum(
                path = checksum.string("path"),
                algorithm = checksum.string("algorithm"),
                value = checksum.string("value"),
            )
        }
        return FrameManifest(
            frameId = root.int("frameId"),
            timestampNanos = root.long("timestampNanos"),
            trackingState = root.string("trackingState"),
            cameraIntrinsics = CameraIntrinsics(
                width = intrinsics.int("width"),
                height = intrinsics.int("height"),
                fx = intrinsics.float("fx"),
                fy = intrinsics.float("fy"),
                cx = intrinsics.float("cx"),
                cy = intrinsics.float("cy"),
            ),
            pose = PoseMatrix(root.list("poseMatrix").map { it.number().toFloat() }),
            quality = FrameQuality(
                depthConfidence = quality.float("depthConfidence"),
                coverage = quality.float("coverage"),
                motionRisk = quality.float("motionRisk"),
                accepted = quality.boolean("accepted"),
                dropReason = quality.optionalString("dropReason"),
            ),
            payloads = FramePayloadRefs(
                colorYuv = payloads.string("colorYuv"),
                depthU16 = payloads.string("depthU16"),
                confidenceU8 = payloads.string("confidenceU8"),
            ),
            checksums = checksums,
        )
    }

    private class JsonBuilder(private val builder: StringBuilder) {
        private var fields = 0

        fun field(name: String, value: String?) {
            nextField(name)
            if (value == null) builder.append("null") else appendString(value)
        }

        fun field(name: String, value: Int) {
            nextField(name)
            appendNumber(value)
        }

        fun field(name: String, value: Long) {
            nextField(name)
            appendNumber(value)
        }

        fun field(name: String, value: Float) {
            nextField(name)
            appendNumber(value)
        }

        fun field(name: String, value: Boolean) {
            nextField(name)
            builder.append(value)
        }

        fun fieldObject(name: String, block: JsonBuilder.() -> Unit) {
            nextField(name)
            builder.appendObject(block)
        }

        fun fieldArray(name: String, block: JsonBuilder.() -> Unit) {
            nextField(name)
            builder.append('[')
            JsonBuilder(builder).block()
            builder.append(']')
        }

        fun comma() {
            builder.append(',')
        }

        fun append(value: String) {
            builder.append(value)
        }

        fun appendNumber(value: Number) {
            builder.append(value.toString())
        }

        fun appendObject(block: JsonBuilder.() -> Unit) {
            builder.appendObject(block)
        }

        private fun nextField(name: String) {
            if (fields > 0) builder.append(',')
            fields += 1
            appendString(name)
            builder.append(':')
        }

        private fun appendString(value: String) {
            builder.append('"')
            value.forEach { char ->
                when (char) {
                    '\\' -> builder.append("\\\\")
                    '"' -> builder.append("\\\"")
                    '\n' -> builder.append("\\n")
                    '\r' -> builder.append("\\r")
                    '\t' -> builder.append("\\t")
                    else -> builder.append(char)
                }
            }
            builder.append('"')
        }
    }

    private fun StringBuilder.appendObject(block: JsonBuilder.() -> Unit) {
        append('{')
        JsonBuilder(this).block()
        append('}')
    }

    private class JsonParser(private val source: String) {
        private var index = 0

        fun parseObject(): Map<String, Any?> {
            val value = parseValue()
            skipWhitespace()
            require(index == source.length) { "Unexpected trailing JSON at $index" }
            return value.asObject()
        }

        private fun parseValue(): Any? {
            skipWhitespace()
            return when (peek()) {
                '{' -> parseMap()
                '[' -> parseList()
                '"' -> parseString()
                't' -> {
                    expect("true")
                    true
                }
                'f' -> {
                    expect("false")
                    false
                }
                'n' -> {
                    expect("null")
                    null
                }
                else -> parseNumber()
            }
        }

        private fun parseMap(): Map<String, Any?> {
            expect('{')
            val map = linkedMapOf<String, Any?>()
            skipWhitespace()
            if (peek() == '}') {
                index++
                return map
            }
            while (true) {
                val key = parseString()
                skipWhitespace()
                expect(':')
                map[key] = parseValue()
                skipWhitespace()
                when (peek()) {
                    ',' -> {
                        index++
                        continue
                    }
                    '}' -> {
                        index++
                        return map
                    }
                    else -> error("Expected comma or object end at $index")
                }
            }
        }

        private fun parseList(): List<Any?> {
            expect('[')
            val list = mutableListOf<Any?>()
            skipWhitespace()
            if (peek() == ']') {
                index++
                return list
            }
            while (true) {
                list += parseValue()
                skipWhitespace()
                when (peek()) {
                    ',' -> {
                        index++
                        continue
                    }
                    ']' -> {
                        index++
                        return list
                    }
                    else -> error("Expected comma or array end at $index")
                }
            }
        }

        private fun parseString(): String {
            expect('"')
            val result = StringBuilder()
            while (index < source.length) {
                val char = source[index++]
                when (char) {
                    '"' -> return result.toString()
                    '\\' -> {
                        val escaped = source[index++]
                        result.append(
                            when (escaped) {
                                '"', '\\', '/' -> escaped
                                'n' -> '\n'
                                'r' -> '\r'
                                't' -> '\t'
                                else -> error("Unsupported escape \\$escaped")
                            },
                        )
                    }
                    else -> result.append(char)
                }
            }
            error("Unterminated string")
        }

        private fun parseNumber(): Number {
            val start = index
            while (index < source.length && source[index] !in charArrayOf(',', '}', ']', ' ', '\n', '\r', '\t')) {
                index++
            }
            val text = source.substring(start, index)
            return if (text.contains('.') || text.contains('e', ignoreCase = true)) text.toDouble() else text.toLong()
        }

        private fun skipWhitespace() {
            while (index < source.length && source[index].isWhitespace()) index++
        }

        private fun peek(): Char = source.getOrNull(index) ?: error("Unexpected JSON end")

        private fun expect(char: Char) {
            skipWhitespace()
            require(peek() == char) { "Expected '$char' at $index" }
            index++
        }

        private fun expect(text: String) {
            require(source.startsWith(text, index)) { "Expected $text at $index" }
            index += text.length
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun Any?.asObject(): Map<String, Any?> = this as? Map<String, Any?> ?: error("Expected JSON object")

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.list(key: String): List<Any?> = this[key] as? List<Any?> ?: error("Expected list field $key")

private fun Map<String, Any?>.obj(key: String): Map<String, Any?> = this[key].asObject()
private fun Map<String, Any?>.string(key: String): String = this[key] as? String ?: error("Expected string field $key")
private fun Map<String, Any?>.optionalString(key: String): String? = this[key] as? String
private fun Map<String, Any?>.int(key: String): Int = numberField(key).toInt()
private fun Map<String, Any?>.long(key: String): Long = numberField(key).toLong()
private fun Map<String, Any?>.float(key: String): Float = numberField(key).toFloat()
private fun Map<String, Any?>.boolean(key: String): Boolean = this[key] as? Boolean ?: error("Expected boolean field $key")
private fun Map<String, Any?>.numberField(key: String): Number = this[key] as? Number ?: error("Expected number field $key")
private fun Any?.number(): Number = this as? Number ?: error("Expected JSON number")
