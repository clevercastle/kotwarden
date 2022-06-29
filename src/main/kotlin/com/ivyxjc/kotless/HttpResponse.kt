package com.ivyxjc.kotless

import kotlinx.serialization.Serializable
import java.util.*


/**
 * HTTP's response ApiGateway representation
 *
 * @param statusCode status code to return
 * @param headers headers to pass to client
 * @param body payload of response
 */
@Serializable
data class HttpResponse(
    val statusCode: Int,
    val headers: HashMap<String, String> = HashMap(),
    val body: String?,
    val isBase64Encoded: Boolean
) {

    constructor(statusCode: Int, headers: HashMap<String, String>, body: String) : this(
        statusCode,
        headers,
        body,
        false
    )

    constructor(statusCode: Int, headers: HashMap<String, String>, body: ByteArray) : this(
        statusCode,
        headers,
        Base64.getEncoder().encodeToString(body),
        true
    )

    constructor(statusCode: Int, mime: MimeType, body: String? = null) : this(
        statusCode,
        hashMapOf("Content-Type" to mime.mimeText),
        body,
        false
    )

    constructor(statusCode: Int, mime: MimeType, body: ByteArray)
            : this(
        statusCode,
        hashMapOf("Content-Type" to mime.mimeText),
        Base64.getEncoder().encodeToString(body),
        true
    ) {
        require(mime.isBinary) { "Base64 encoded response can be used only for binary types" }
    }
}

