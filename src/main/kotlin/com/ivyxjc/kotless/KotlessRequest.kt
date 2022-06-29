package com.ivyxjc.kotless

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.utils.io.*

/**
 * Ktor Request used by Kotless. It will be created from APIGateway request.
 */
class KotlessRequest(val query: HttpRequest, call: ApplicationCall) : BaseApplicationRequest(call) {
    override val pipeline = ApplicationReceivePipeline().apply {
        merge(call.application.receivePipeline)
    }

    override val cookies: RequestCookies = RequestCookies(this)

    override val headers: Headers = Headers.build {
        query.headers.forEach { appendAll(it.key, listOf(it.value)) }
    }

    override val local: RequestConnectionPoint = object : RequestConnectionPoint {
        override val host: String = query.context.domain
        override val method: io.ktor.http.HttpMethod = io.ktor.http.HttpMethod.parse(query.method.name)

        //Port is not applicable in case of Serverless execution
        override val port: Int = -1
        override val remoteHost: String = query.context.sourceIp
        override val scheme: String = query.context.protocol
        override val uri: String = query.path
        override val version: String = query.context.protocol
    }

    override val queryParameters: Parameters = Parameters.build {
        query.params.forEach { append(it.key, it.value) }
    }

    override val rawQueryParameters: Parameters
        get() = TODO("Not yet implemented")

    override fun receiveChannel() = ByteReadChannel(query.body?.bytes ?: ByteArray(0))
}
