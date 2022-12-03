package org.clevercastle.kotwarden.web.controller

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import org.clevercastle.kotwarden.model.Cipher
import org.clevercastle.kotwarden.model.Organization
import org.clevercastle.kotwarden.model.VaultCollection
import org.clevercastle.kotwarden.web.kotwardenPrincipal
import org.clevercastle.kotwarden.web.model.CipherCollectionsRequestModel
import org.clevercastle.kotwarden.web.model.CipherDetailsResponseModelListResponseModel
import org.clevercastle.kotwarden.web.model.CollectionDetailsResponseModelListResponseModel
import org.clevercastle.kotwarden.web.model.CollectionRequestModel
import org.clevercastle.kotwarden.web.model.OrganizationCreateRequestModel
import org.clevercastle.kotwarden.web.model.OrganizationUpdateRequestModel
import org.clevercastle.kotwarden.web.model.OrganizationUserBulkResponseModelListResponseModel
import org.clevercastle.kotwarden.web.model.OrganizationUserResponseModel
import org.clevercastle.kotwarden.web.model.PlanResponseModel
import org.clevercastle.kotwarden.web.service.CipherService
import org.clevercastle.kotwarden.web.service.CollectionService
import org.clevercastle.kotwarden.web.service.OrganizationService

class OrganizationController(
    private val organizationService: OrganizationService,
    private val cipherService: CipherService,
    private val collectionService: CollectionService
) {
    suspend fun getPlans(ctx: ApplicationCall) {
        ctx.apply {
            val json = buildJsonObject {
                put("object", "list")
                put("continuationToken", null as String?)
                putJsonArray("data") {
                    add(buildJsonObject {
                        put("object", "plan")
                        put("type", 0)
                        put("product", 0)
                        put("name", "Free")
                        put("nameLocalizationKey", "planNameFree")
                        put("descriptionLocalizationKey", "planDescFree")
                    })
                }

            }
            this.respond(HttpStatusCode.OK, json)
        }
    }

    suspend fun updateCipherCollections(cipherId: String, ctx: ApplicationCall) {
        ctx.apply {
            val request = ctx.receive<CipherCollectionsRequestModel>()
            organizationService.updateCipherCollections(cipherId, request)
            this.respond(HttpStatusCode.OK)
        }
    }

    suspend fun listOrganizations(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val list = organizationService.listByUserId(principal.id)
                .map { Organization.converter.toProfileResponse(it.second) }
            this.respond(HttpStatusCode.OK, list)
        }
    }

    suspend fun listOrganizationDetail(organizationId: String, ctx: ApplicationCall) {
        ctx.apply {
            val ciphers = cipherService.listByOrganization(organizationId)
                .map {
                    val resp = Cipher.converter.toCipherDetailResponse(it)
                    resp.collectionIds = collectionService.listCollectionIdsByCipher(it.id).map { t -> t.collectionId }
                    return@map resp
                }
            val res = CipherDetailsResponseModelListResponseModel()
            res.xyObject = "list"
            res.data = ciphers
            res.continuationToken = null
            this.respond(HttpStatusCode.OK, res)
        }
    }


    suspend fun listCollectionsByOrganization(organizationId: String, ctx: ApplicationCall) {
        ctx.apply {
            val list = organizationService.listCollectionByOrganization(organizationId).map {
                VaultCollection.converter.toResponse(it)
            }
            val res = CollectionDetailsResponseModelListResponseModel()
            res.xyObject = "list"
            res.continuationToken = null
            res.data = list
            this.respond(res)
        }
    }

    suspend fun listCollectionsByUser(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val list =
                organizationService.listCollectionByUser(principal.id).map { VaultCollection.converter.toResponse(it) }
            val res = CollectionDetailsResponseModelListResponseModel()
            res.xyObject = "list"
            res.continuationToken = null
            res.data = list
            this.respond(res)
        }
    }

    suspend fun createCollection(organizationId: String, ctx: ApplicationCall) {
        ctx.apply {
            val req = ctx.receive<CollectionRequestModel>()
            organizationService.createCollection(organizationId, req)
            this.respond(HttpStatusCode.OK, "")
        }
    }

    suspend fun listUsers(organizationId: String, ctx: ApplicationCall) {
        ctx.apply {
            val list = organizationService.listUserOrganizationsByOrganization(organizationId)
            val resp = OrganizationUserBulkResponseModelListResponseModel()
            resp.xyObject = "list"
            resp.continuationToken = null
            resp.data = list.map {
                val res = OrganizationUserResponseModel()
                res.xyObject = "organizationUserUserDetails"
                res.id = it.first.organizationId
                res.userId = it.first.userId
                res.name = it.second.name
                res.email = it.second.email
                res.status = it.first.status
                res.type = it.first.type
                res.accessAll = it.first.accessAll
                return@map res
            }
            this.respond(resp)
        }
    }

    suspend fun createOrganization(ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<OrganizationCreateRequestModel>()
            val organization = organizationService.createOrganization(principal, request)
            val json = buildJsonObject {
                put("object", "organization")
                put("id", organization.id)
                put("name", organization.name)
                put("billingEmail", organization.billingEmail)
                put("identifier", null as String?)
                put("businessName", null as String?)
                put("businessAddress1", null as String?)
                put("businessAddress2", null as String?)
                put("businessAddress3", null as String?)
                put("businessCountry", null as String?)
                put("businessTaxNumber", null as String?)
                put("planType", 0)
                put("seats", 100)
                put("maxAutoscaleSeats", null as String?)
                put("maxCollections", 2)
                put("maxStorageGb", null as String?)
                put("usePolicies", false)
                put("useSso", false)
                put("useKeyConnector", false)
                put("useGroups", false)
                put("useDirectory", false)
                put("useEvents", false)
                put("useTotp", false)
                put("use2fa", false)
                put("useApi", false)
                put("useResetPassword", false)
                put("usersGetPremium", false)
                put("selfHost", false)
                put("hasPublicAndPrivateKeys", true)
                put(
                    "plan", Json.encodeToJsonElement(
                        PlanResponseModel(
                            `object` = "plan",
                            type = 0,
                            product = 0,
                            name = "Free", nameLocalizationKey = "planNameFree",
                            descriptionLocalizationKey = "planDescFree",
                            baseSeats = 100,
                            maxCollections = 100,
                            maxUsers = 100,
                        )
                    )

                )
            }
            this.respond(HttpStatusCode.OK, json)
        }
    }

    suspend fun getOrganization(id: String, ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val organization = organizationService.getOrganization(principal, id)
            val resp = Organization.converter.toResponse(organization)
            resp.usersGetPremium = true
            this.respond(HttpStatusCode.OK, resp)
        }
    }

    suspend fun updateOrganization(id: String, ctx: ApplicationCall) {
        ctx.apply {
            val principal = kotwardenPrincipal(this)
            val request = this.receive<OrganizationUpdateRequestModel>()
            val organization = organizationService.updateOrganization(principal, id, request)
            this.respond(HttpStatusCode.OK, Organization.converter.toResponse(organization))
        }

    }
}