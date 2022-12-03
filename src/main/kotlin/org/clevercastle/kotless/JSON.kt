package org.clevercastle.kotless

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

object JSON {
    val json = Json {
        ignoreUnknownKeys = true
    }

    inline fun <reified T : Any> string(serializer: KSerializer<T>, obj: T): String =
        json.encodeToString(serializer, obj)

    inline fun <reified T : Any> bytes(serializer: KSerializer<T>, obj: T): ByteArray =
        string(serializer, obj).toByteArray()

    inline fun <reified T : Any> parse(serializer: KSerializer<T>, serialized: String): T =
        json.decodeFromString(serializer, serialized)
}

