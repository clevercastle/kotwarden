package org.clevercastle.kotwarden.web

import io.ktor.http.HttpStatusCode

open class KotwardenException : RuntimeException {
    val httpCode: HttpStatusCode

    constructor(code: HttpStatusCode, message: String, ex: Exception) : super(message, ex) {
        this.httpCode = code
    }

    constructor(code: HttpStatusCode, message: String) : super(message) {
        this.httpCode = code
    }
}

class NotAuthorizedException : KotwardenException {
    constructor(code: HttpStatusCode, message: String, ex: Exception) : super(code, message, ex)

    constructor(code: HttpStatusCode, message: String) : super(code, message)

}

fun notAuthorized(code: HttpStatusCode, message: Any): Nothing = throw NotAuthorizedException(code, message.toString())
