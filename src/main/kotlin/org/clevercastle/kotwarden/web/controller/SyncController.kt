package org.clevercastle.kotwarden.web.controller

import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import org.clevercastle.kotwarden.web.kotwardenPrincipal
import org.clevercastle.kotwarden.web.service.SyncService

class SyncController(private val syncService: SyncService) {

    suspend fun sync(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            this.respond(syncService.sync(principal, principal.id))
        }
    }

}