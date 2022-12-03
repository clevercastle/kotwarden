@file:JvmName(name = "JsonUtil")

package org.clevercastle.kotwarden.util

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> decodeFromString(data: String?): T? {
    return if (data == null) {
        null
    } else {
        Json.decodeFromString(data)
    }
}

inline fun <reified T> encodeToString(data: T): String? {
    return if (data == null) {
        null
    } else {
        Json.encodeToString(data)
    }
}

