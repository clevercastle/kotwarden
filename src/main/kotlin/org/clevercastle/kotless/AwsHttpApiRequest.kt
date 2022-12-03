package org.clevercastle.kotless

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/** HTTP's request ApiGateway v2 http api representation */
@Serializable
data class AwsHttpApiRequest(
    @SerialName("headers") val myHeaders: Map<String, String>?,
    @SerialName("queryStringParameters") val myQueryStringParameters: Map<String, String>? = mapOf(),
    val pathParameters: Map<String, String>?,
    val requestContext: RequestContext,
    @SerialName("body") val myBody: String? = null,
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
        val accountId: String,
        val apiId: String,
        val domainName: String,
        val domainPrefix: String,
        val http: RequestContextHttp,
        val requestId: String,
        val routeKey: String,
        val stage: String,
        val time: String,
        val timeEpoch: Long
    ) {
        @Serializable
        data class RequestContextHttp(
            val method: HttpMethod,
            val path: String,
            val protocol: String,
            val sourceIp: String,
            val userAgent: String
        )
    }

    fun toRequest(): HttpRequest {
        return HttpRequest(
            requestContext.http.path, requestContext.http.method, params.orEmpty(),
            headers.orEmpty().mapValues { it.value.joinToString(separator = ", ") },
            body?.let { HttpRequest.Content(it) },
            HttpRequest.Context(requestContext.domainName, requestContext.http.protocol, requestContext.http.sourceIp)
        )
    }
}

