package org.clevercastle.kotwarden.web

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import org.clevercastle.kotwarden.web.model.KotwardenPrincipal

fun kotwardenPrincipal(call: ApplicationCall): KotwardenPrincipal {
    val principal = call.principal<JWTPrincipal>()
    val id = principal!!.payload.getClaim("id").asString()
    val email = principal.payload.getClaim("email").asString()
    return KotwardenPrincipal(id, email)
}

fun kError(status: HttpStatusCode, message: String): Nothing {
    throw KotwardenException(status, message)
}

fun kError(message: String): Nothing {
    throw KotwardenException(HttpStatusCode.InternalServerError, message)
}
