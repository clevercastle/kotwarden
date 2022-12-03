package org.clevercastle.kotless

import io.kotless.dsl.ktor.app.KotlessResponse
import io.ktor.http.Parameters
import io.ktor.server.application.Application
import io.ktor.server.engine.BaseApplicationCall

class KotlessCall(application: Application, request: HttpRequest) : BaseApplicationCall(application) {
    override val request = KotlessRequest(request, this)
    override val response = KotlessResponse(this)

    override val parameters: Parameters by lazy { this.request.queryParameters }

    init {
        putResponseAttribute()
    }
}
