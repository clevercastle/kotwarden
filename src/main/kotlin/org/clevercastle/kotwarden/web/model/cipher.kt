package org.clevercastle.kotwarden.web.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonObject
import org.clevercastle.kotwarden.model.serializer.OffsetDatetimeNullableSerializer
import java.time.OffsetDateTime

// 1: Login; 2: Secure Note; 3: Card; 4: Identity
typealias CipherType = Int
typealias CipherRepromptType = Int

// region cipher request
@kotlinx.serialization.Serializable
data class CipherRequestModel(
    val name: String,
    val type: CipherType,
    val organizationId: String? = null,
    var folderId: String? = null,
    val favorite: Boolean? = null,
    val reprompt: Int? = null,
    val notes: String? = null,
    val fields: List<CipherFieldModel>? = null,
    val passwordHistory: List<CipherPasswordHistoryModel>? = null,
    val attachments: Map<String, String>? = null,
    val attachments2: Map<String, CipherAttachmentModel>? = null,
    val login: CipherLoginModel? = null,
    val card: CipherCardModel? = null,
    val identity: CipherIdentityModel? = null,
    val secureNote: CipherSecureNoteModel? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class) var lastKnownRevisionDate: OffsetDateTime? = null
)

@kotlinx.serialization.Serializable
data class CipherCreateRequestModel(
    val cipher: CipherRequestModel, val collectionIds: List<String>? = null
)

@kotlinx.serialization.Serializable
data class ImportCiphersRequestModel(
    val folders: List<FolderRequestModel>? = null,
    val ciphers: List<CipherRequestModel>? = null,
    val folderRelationships: List<KeyValuePair>? = null
)

@kotlinx.serialization.Serializable
data class KeyValuePair(
    val key: Int? = null,
    val value: Int? = null
)


@kotlinx.serialization.Serializable
data class CipherFieldModel(
    val type: CipherType? = null, val name: String? = null, val value: String? = null
)

@kotlinx.serialization.Serializable
data class CipherPasswordHistoryModel(
    val password: String,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class) val lastUsedDate: OffsetDateTime?
)

@kotlinx.serialization.Serializable
data class CipherAttachmentModel(
    val fileName: String? = null, val key: String? = null
)

@kotlinx.serialization.Serializable
data class CipherLoginModel(
    val uri: String? = null,
    val uris: List<CipherLoginUriModel>? = null,
    val username: String? = null,
    val password: String? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class) val passwordRevisionDate: OffsetDateTime? = null,
    val totp: String? = null,
    val autofillOnPageLoad: Boolean? = null
)

@kotlinx.serialization.Serializable
data class CipherCardModel(
    val cardholderName: String? = null,
    val brand: String? = null,
    val number: String? = null,
    val expMonth: String? = null,
    val expYear: String? = null,
    val code: String? = null
)

@kotlinx.serialization.Serializable
data class CipherIdentityModel(
    val title: String? = null,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val address1: String? = null,
    val address2: String? = null,
    val address3: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val country: String? = null,
    val company: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val ssn: String? = null,
    val username: String? = null,
    val passportNumber: String? = null,
    val licenseNumber: String? = null
)

@kotlinx.serialization.Serializable
data class CipherSecureNoteModel(
    val type: CipherType? = null
)

@kotlinx.serialization.Serializable
data class CipherLoginUriModel(
    val uri: String? = null, val match: Int? = null
)

@kotlinx.serialization.Serializable
data class CipherBulkDeleteRequestModel(
    val ids: List<String>, val organizationId: String? = null
)
// endregion


// region cipher response
@kotlinx.serialization.Serializable
data class CipherResponseModel(
    val folderId: String? = null,
    val favorite: Boolean? = null,
    var edit: Boolean? = null,
    var viewPassword: Boolean? = null,
    val id: String? = null,
    val organizationId: String? = null,
    val type: CipherType? = null,
    var data: JsonObject? = null,
    val name: String? = null,
    val notes: String? = null,
    var login: CipherLoginModel? = null,
    var card: CipherCardModel? = null,
    var identity: CipherIdentityModel? = null,
    var secureNote: CipherSecureNoteModel? = null,
    var fields: List<CipherFieldModel>? = null,
    var passwordHistory: List<CipherPasswordHistoryModel>? = null,
    var attachments: List<AttachmentResponseModel>? = null,
    val organizationUseTotp: Boolean? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class) var revisionDate: OffsetDateTime? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class) val deletedDate: OffsetDateTime? = null,
    val reprompt: Int? = null,
    @SerialName("object") var xObject: String? = null
)

@kotlinx.serialization.Serializable
data class AttachmentResponseModel(
    val id: String? = null,
    val url: String? = null,
    val fileName: String? = null,
    val key: String? = null,
    val size: String? = null,
    val sizeName: String? = null,
    @SerialName("object") val xObject: String? = null
)

// endregion