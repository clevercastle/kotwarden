package org.clevercastle.kotwarden.web.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonObject
import org.clevercastle.kotwarden.model.serializer.OffsetDatetimeNullableSerializer
import java.time.OffsetDateTime

@kotlinx.serialization.Serializable
data class SyncResponseModel(
    @SerialName("object")
    var xyObject: String? = null,
    var profile: ProfileResponseModel? = null,
    val folders: MutableList<FolderResponseModel> = mutableListOf(),
    val collections: MutableList<CollectionDetailsResponseModel> = mutableListOf(),
    val ciphers: MutableList<CipherDetailsResponseModel> = mutableListOf(),
    var domains: DomainsResponseModel? = null,
//    var policies: List<PolicyResponseModel>? = null,
//    var sends: List<SendResponseModel>? = null
)




@kotlinx.serialization.Serializable
data class CollectionDetailsResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val id: String? = null,
    val organizationId: String? = null,
    val name: String? = null,
    val externalId: String? = null,
    val readOnly: Boolean? = null,
    val hidePasswords: Boolean? = null
)

@kotlinx.serialization.Serializable
data class CipherDetailsResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val id: String? = null,
    val organizationId: String? = null,
    val type: CipherType? = null,
    // TODO: 2022/7/2 check type
    var data: JsonObject? = null,
    val name: String? = null,
    val notes: String? = null,
    var login: CipherLoginModel? = null,
    var card: CipherCardModel? = null,
    var identity: CipherIdentityModel? = null,
    var secureNote: CipherSecureNoteModel? = null,
    val fields: List<CipherFieldModel>? = null,
    var passwordHistory: List<CipherPasswordHistoryModel>? = null,
    val attachments: List<AttachmentResponseModel>? = null,
    val organizationUseTotp: Boolean? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class)
    val revisionDate: OffsetDateTime? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class)
    val deletedDate: OffsetDateTime? = null,
    val reprompt: CipherRepromptType? = null,
    val folderId: String? = null,
    val favorite: Boolean? = null,
    val edit: Boolean? = null,
    val viewPassword: Boolean? = null,
    var collectionIds: List<String>? = null
)


@kotlinx.serialization.Serializable
data class DomainsResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val equivalentDomains: List<List<String>>? = null,
    val globalEquivalentDomains: List<GlobalDomains>? = null
)

@kotlinx.serialization.Serializable()
data class GlobalDomains(
    val type: GlobalEquivalentDomainsType? = null,
    val domains: List<String>? = null,
    val excluded: Boolean? = null
)

data class PolicyResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val id: String? = null,
    val organizationId: String? = null,
    val type: PolicyType? = null,
    val data: Map<String, Any>? = null,
    val enabled: Boolean? = null
)

data class SendResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val id: String? = null,
    val accessId: String? = null,
    val type: SendType? = null,
    val name: String? = null,
    val notes: String? = null,
    val file: SendFileModel? = null,
    val text: SendTextModel? = null,
    val key: String? = null,
    val maxAccessCount: Int? = null,
    val accessCount: Int? = null,
    val password: String? = null,
    val disabled: Boolean? = null,
    val revisionDate: java.time.LocalDateTime? = null,
    val expirationDate: java.time.LocalDateTime? = null,
    val deletionDate: java.time.LocalDateTime? = null,
    val hideEmail: Boolean? = null
)

