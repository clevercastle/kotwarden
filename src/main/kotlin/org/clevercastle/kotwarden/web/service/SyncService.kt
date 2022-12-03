package org.clevercastle.kotwarden.web.service

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.clevercastle.kotwarden.model.Cipher
import org.clevercastle.kotwarden.model.Folder
import org.clevercastle.kotwarden.model.Organization
import org.clevercastle.kotwarden.model.User
import org.clevercastle.kotwarden.model.UserOrganization
import org.clevercastle.kotwarden.model.VaultCollection
import org.clevercastle.kotwarden.util.decodeFromString
import org.clevercastle.kotwarden.util.format
import org.clevercastle.kotwarden.web.model.KotwardenPrincipal
import org.clevercastle.kotwarden.web.model.SyncResponseModel
import org.clevercastle.kotwarden.web.notAuthorized

class SyncService(
    private val accountService: AccountService,
    private val cipherService: CipherService,
    private val folderService: FolderService,
    private val organizationService: OrganizationService,
    private val collectionService: CollectionService
) {
    fun sync(principal: KotwardenPrincipal, userId: String): SyncResponseModel {
        val resp = SyncResponseModel()
        resp.xyObject = "sync"
        val user = accountService.findById(principal.id) ?: notAuthorized(HttpStatusCode.Unauthorized, "User not found")

        resp.profile = User.converter.toProfileResponse(user)
        resp.profile!!.emailVerified = true
        resp.profile!!.organizations =
            organizationService.listByUserId(user.id).map {
                val resp = Organization.converter.toProfileResponse(it.second)
                resp.userId = user.id
                resp.enabled = true
                resp.type = UserOrganization.Type.Owner
                resp.status = UserOrganization.Status.Confirmed
                resp.key = it.first.key
                resp.hasPublicAndPrivateKeys = it.second.publicKey != null && it.second.encryptedPrivateKey != null
                return@map resp
            }
        val ciphers = cipherService.listAllByUser(principal.id)
        val folders = folderService.listByUser(principal.id)
        resp.ciphers.addAll(ciphers.map {
            val r = Cipher.converter.toCipherDetailResponse(it)
            when (r.type) {
                1 -> r.login = decodeFromString(it.data)
                2 -> r.secureNote = decodeFromString(it.data)
                3 -> r.card = decodeFromString(it.data)
                4 -> r.identity = decodeFromString(it.data)
                else -> error("Invalid type")
            }
            r.data = toSome(it)
            if (it.passwordHistory == null) {
                r.passwordHistory = null
            } else {
                r.passwordHistory = decodeFromString(it.passwordHistory!!)
            }
            r.collectionIds = collectionService.listCollectionIdsByCipher(it.id).map { t -> t.collectionId }
            return@map r
        })
        resp.folders.addAll(folders.map {
            Folder.converter.toFolderResponse(it)
        })
        resp.profile!!.organizations!!.forEach {
            resp.collections.addAll(
                organizationService.listCollectionByOrganization(it.id!!)
                    .map { VaultCollection.converter.toResponse(it) })
        }
        return resp
    }


}

fun toSome(cipher: Cipher): JsonObject {
    return buildJsonObject {
        put("id", cipher.id)
        put("name", cipher.name)
        put("type", cipher.type)
        put("favorite", cipher.favorite)
        put("createdAt", format(cipher.createdAt))
        put("updatedAt", format(cipher.updatedAt))
        if (cipher.fields != null) {
            put("fields", decodeFromString(cipher.fields)!!)
        }
        decodeFromString<JsonObject>(cipher.data)!!.mapKeys { it.key }.forEach { (k, v) ->
            put(k, v)
        }
    }
}