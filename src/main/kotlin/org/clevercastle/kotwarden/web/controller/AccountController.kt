package org.clevercastle.kotwarden.web.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.clevercastle.kotwarden.model.User
import org.clevercastle.kotwarden.web.kotwardenPrincipal
import org.clevercastle.kotwarden.web.model.KdfRequestModel
import org.clevercastle.kotwarden.web.model.PreLoginRequest
import org.clevercastle.kotwarden.web.model.RegisterRequest
import org.clevercastle.kotwarden.web.service.AccountService

class AccountController(private val accountService: AccountService) {
    suspend fun preLogin(ctx: ApplicationCall) {
        ctx.receive<PreLoginRequest>().apply {
            val preLoginResponse = accountService.preLogin(this)
            ctx.respond(HttpStatusCode.OK, preLoginResponse)
        }

    }

    suspend fun register(ctx: ApplicationCall) {
        ctx.receive<RegisterRequest>().apply {
            accountService.register(this)
            ctx.respond(HttpStatusCode.OK, Unit)
        }
    }

    suspend fun profile(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val user = accountService.findById(principal.id)
            if (user == null) {
                ctx.respond(HttpStatusCode.NotFound, "Fail to find the user")
            }
            ctx.respond(HttpStatusCode.OK, User.converter.toProfileResponse(user!!))
        }
    }

    suspend fun updateKdf(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = ctx.receive<KdfRequestModel>()
            accountService.updateKdf(principal.id, request)
            ctx.respond(HttpStatusCode.OK)
        }
    }
}