package org.clevercastle.kotwarden.web.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.clevercastle.kotwarden.model.Folder
import org.clevercastle.kotwarden.web.kotwardenPrincipal
import org.clevercastle.kotwarden.web.model.FolderRequestModel
import org.clevercastle.kotwarden.web.service.FolderService

class FolderController(val folderService: FolderService) {
    suspend fun createFolder(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<FolderRequestModel>()
            ctx.respond(
                HttpStatusCode.OK,
                Folder.converter.toFolderResponse(folderService.createFolder(principal, request))
            )
        }
    }

    suspend fun deleteFolder(ctx: ApplicationCall, id: String) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            this.respond(folderService.deleteFolder(principal, id))
            ctx.respond(HttpStatusCode.OK)
        }
    }


    suspend fun updateFolder(ctx: ApplicationCall, id: String) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<FolderRequestModel>()
            this.respond(HttpStatusCode.OK, folderService.updateFolder(principal, id, request))
        }
    }

    suspend fun getFolder(ctx: ApplicationCall, id: String) {

        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<FolderRequestModel>()
            this.respond(Folder.converter.toFolderResponse(folderService.createFolder(principal, request)))
        }
    }
}