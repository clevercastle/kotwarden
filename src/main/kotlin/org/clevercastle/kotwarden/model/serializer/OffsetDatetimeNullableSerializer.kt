package org.clevercastle.kotwarden.model.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.clevercastle.kotwarden.util.format
import org.clevercastle.kotwarden.util.parse
import java.time.OffsetDateTime

class OffsetDatetimeNullableSerializer : KSerializer<OffsetDateTime?> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("OffsetDatetime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime? {
        return parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime?) {
        encoder.encodeString(format(value))
    }
}

class OffsetDatetimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("OffsetDatetime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return parse(decoder.decodeString())!!
    }

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(format(value))
    }
}