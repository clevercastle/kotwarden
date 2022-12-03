package org.clevercastle.kotless

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/** HTTP's request ApiGateway representation */
@Serializable
data class AwsApiRequest(
    val resource: String,
    val path: String,
    @SerialName("httpMethod") val method: org.clevercastle.kotless.HttpMethod,
    @SerialName("headers") val myHeaders: Map<String, String>?,
    @SerialName("queryStringParameters") val myQueryStringParameters: Map<String, String>?,
    val pathParameters: Map<String, String>?,
    val requestContext: org.clevercastle.kotless.AwsApiRequest.RequestContext,
    @SerialName("body") val myBody: String?,
    private val isBase64Encoded: Boolean
) {

    val headers: Map<String, List<String>>?
        get() = myHeaders?.mapValues { (_, value) -> value.split(",").map { it.trim() } }

    val params = myQueryStringParameters

    val body: ByteArray?
        get() = myBody?.let {
            if (isBase64Encoded) {
                Base64.getDecoder().decode(it)
            } else {
                it.toByteArray()
            }
        }

    @Serializable
    data class RequestContext(
        val resourcePath: String,
        val path: String,
        val accountId: String,
        val resourceId: String,
        val stage: String,
        val identity: org.clevercastle.kotless.AwsApiRequest.RequestContext.RequestIdentity,
        val protocol: String,
        val requestTimeEpoch: Long,
        val domainName: String
    ) {
        //Path to stage is calculated as full path minus path to resource and first /
        val stagePath = path.dropLast(resourcePath.length - 1)

        @Serializable
        data class RequestIdentity(val sourceIp: String, val userAgent: String?)
    }

    fun toRequest(): org.clevercastle.kotless.HttpRequest {
        return org.clevercastle.kotless.HttpRequest(
            path, method, params.orEmpty(),
            headers.orEmpty().mapValues { it.value.joinToString(separator = ", ") },
            body?.let { org.clevercastle.kotless.HttpRequest.Content(it) },
            org.clevercastle.kotless.HttpRequest.Context(
                requestContext.domainName,
                requestContext.protocol,
                requestContext.identity.sourceIp
            )
        )
    }
}

