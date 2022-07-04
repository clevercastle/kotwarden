package com.ivyxjc.kotwarden.web

import com.ivyxjc.kotwarden.web.model.KotwardenPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


fun kotwardenPrincipal(call: ApplicationCall): KotwardenPrincipal {
    val principal = call.principal<JWTPrincipal>()
    val id = principal!!.payload.getClaim("id").asString()
    val email = principal.payload.getClaim("email").asString()
    return KotwardenPrincipal(id, email)
}

typealias ErrorCode = String

fun kError(status: HttpStatusCode, code: ErrorCode, message: String) {
    throw IllegalStateException(message.toString())
}

fun kError(message: String): Nothing {
    throw IllegalStateException(message.toString())
}
