package org.clevercastle.kotwarden.web.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import org.clevercastle.kotwarden.model.TwoFactor
import org.clevercastle.kotwarden.web.kotwardenPrincipal
import org.clevercastle.kotwarden.web.model.TwoFactorProviderResponseModelListResponseModel
import org.clevercastle.kotwarden.web.service.TwoFactorService

class TwoFactorController(private val twoFactorService: TwoFactorService) {
    suspend fun twoFactor(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val list = twoFactorService.getByUser(principal.id)
            val resp = TwoFactorProviderResponseModelListResponseModel(
                xyObject = "list",
                data = list.map { TwoFactor.converter.toProviderResponse(it) }
            )
            ctx.respond(HttpStatusCode.OK, resp)
        }
    }
}