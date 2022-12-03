package org.clevercastle.kotwarden.web.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.clevercastle.kotwarden.model.Cipher
import org.clevercastle.kotwarden.util.decodeFromString
import org.clevercastle.kotwarden.util.isEmpty
import org.clevercastle.kotwarden.util.isNotEmpty
import org.clevercastle.kotwarden.web.kError
import org.clevercastle.kotwarden.web.kotwardenPrincipal
import org.clevercastle.kotwarden.web.model.CipherBulkDeleteRequestModel
import org.clevercastle.kotwarden.web.model.CipherCreateRequestModel
import org.clevercastle.kotwarden.web.model.CipherRequestModel
import org.clevercastle.kotwarden.web.model.ImportCiphersRequestModel
import org.clevercastle.kotwarden.web.model.SensitiveActionRequestModel
import org.clevercastle.kotwarden.web.service.CipherService
import org.clevercastle.kotwarden.web.service.CollectionService
import org.clevercastle.kotwarden.web.service.toSome

class CipherController(
    private val cipherService: CipherService, private val collectionService: CollectionService
) {

    suspend fun getCipher(id: String, ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            // TODO: 2022/7/24 check permission
            ctx.apply {
                val cipher = cipherService.get(id)
                if (cipher == null) {
                    this.respond(HttpStatusCode.NotFound)
                } else {
                    val resp = Cipher.converter.toResponse(cipher)
                    when (resp.type) {
                        1 -> resp.login = decodeFromString(cipher.data)
                        2 -> resp.secureNote = decodeFromString(cipher.data)
                        3 -> resp.card = decodeFromString(cipher.data)
                        4 -> resp.identity = decodeFromString(cipher.data)
                        else -> error("Invalid type")
                    }
                    resp.data = toSome(cipher)
                    if (cipher.passwordHistory == null) {
                        resp.passwordHistory = null
                    } else {
                        resp.passwordHistory = decodeFromString(cipher.passwordHistory!!)
                    }
                    this.respond(resp)
                }
            }
        }
    }

    suspend fun createCipher(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<CipherRequestModel>()
            this.respond(cipherService.createCipher(principal, request))
        }
    }


    /**
     * Called when creating a new org-owned cipher, or cloning a cipher (whether
     * user- or org-owned). When cloning a cipher to a user-owned cipher,
     * `organizationId` is null.
     */
    suspend fun createCipherRequest(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<CipherCreateRequestModel>()
            if (isNotEmpty(request.cipher.organizationId) && isEmpty(request.collectionIds)) {
                kError("You must select at least one collection.")
            }
            this.respond(cipherService.createShareCipher(principal, request))
        }
    }

    suspend fun shareCipher(id: String, ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<CipherCreateRequestModel>()
            this.respond(cipherService.shareCipher(principal, id, request))
        }
    }

    suspend fun deleteCipher(ctx: ApplicationCall, id: String) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            this.respond(cipherService.deleteCipher(principal, id))
        }
    }

    suspend fun deleteCiphers(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<CipherBulkDeleteRequestModel>()
            cipherService.deleteCiphers(principal, request)
            this.respond(HttpStatusCode.OK)
        }
    }

    suspend fun updateCipher(ctx: ApplicationCall, cipherId: String) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<CipherRequestModel>()
            this.respond(cipherService.updateCipher(principal, cipherId, request))
        }
    }

    suspend fun importCiphers(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<ImportCiphersRequestModel>()
            cipherService.importCiphers(principal, request)
            this.respond(HttpStatusCode.OK)
        }
    }

    suspend fun purge(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = ctx.receive<SensitiveActionRequestModel>()
            cipherService.purge(principal.id, request)
            this.respond(HttpStatusCode.OK)
        }
    }
}