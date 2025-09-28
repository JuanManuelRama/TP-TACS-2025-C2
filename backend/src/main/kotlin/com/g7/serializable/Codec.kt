package com.g7.serializable

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class DurationCodec : Codec<Duration> {
    override fun encode(writer: BsonWriter, value: Duration, encoderContext: EncoderContext) {
        println("${value.inWholeMilliseconds}ms")
        writer.writeInt64(value.inWholeMilliseconds)
        println("Done")
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): Duration {
        println("${reader.readInt64()}")
        return reader.readInt64().milliseconds
    }

    override fun getEncoderClass(): Class<Duration> {
        println("Getting encoder class")
        return Duration::class.java
    }
}
