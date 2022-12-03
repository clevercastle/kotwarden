package org.clevercastle.kotwarden.model

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import org.clevercastle.kotwarden.util.EMPTY_STRING
import org.clevercastle.kotwarden.util.decodeFromString
import org.clevercastle.kotwarden.util.encodeToString
import org.clevercastle.kotwarden.util.isEmpty
import org.clevercastle.kotwarden.web.model.*
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.time.OffsetDateTime

@DynamoDbBean
class Cipher {
    companion object {
        const val TABLE_NAME = "resource"
        const val SK_INDEX = "SK-Index"
        const val FOLDER_SK_INDEX = "Folder-SK-Index"
        val converter: CipherConverter = Mappers.getMapper(CipherConverter::class.java)
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    lateinit var ownerId: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    @get:DynamoDbSecondaryPartitionKey(indexNames = [SK_INDEX])
    @get:DynamoDbSecondarySortKey(indexNames = [FOLDER_SK_INDEX])
    lateinit var id: String

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime


    @get:DynamoDbAttribute("OrganizationId")
    var organizationId: String? = null

    @get:DynamoDbAttribute("Type")
    var type: CipherType = 0

    @get:DynamoDbAttribute("Name")
    lateinit var name: String

    @get:DynamoDbAttribute("Notes")
    var notes: String? = null

    @get:DynamoDbAttribute("Fields")
    var fields: String? = null

    @get:DynamoDbAttribute("Data")
    lateinit var data: String

    @get:DynamoDbAttribute("PasswordHistory")
    var passwordHistory: String? = null

    @get:DynamoDbAttribute("DeletedAt")
    var deletedAt: OffsetDateTime? = null

    @get:DynamoDbAttribute("Reprompt")
    var reprompt: Int? = null

    // cipher folder
    @get:DynamoDbAttribute("FolderId")
    @get:DynamoDbSecondaryPartitionKey(indexNames = [FOLDER_SK_INDEX])
    var folderId: String? = null

    @get:DynamoDbAttribute("Favorite")
    var favorite: Boolean = false
}

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@JvmDefaultWithCompatibility
interface CipherConverter {
    fun mapField(field: String?): List<CipherFieldModel>? {
        if (isEmpty(field)) {
            return decodeFromString(field)
        }
        return decodeFromString<List<CipherFieldModel>>(field!!)
    }

    fun mapField(list: List<CipherFieldModel>?): String? {
        return encodeToString(list)
    }

    fun mapData(data: String): JsonObject? {
        return buildJsonObject { }
    }

    fun mapData(map: JsonObject?): String? {
        return EMPTY_STRING
    }

    fun mapPasswordHistory(data: String?): List<CipherPasswordHistoryModel> {
        return listOf()
    }

    fun mapPasswordHistory(data: List<CipherPasswordHistoryModel>?): String? {
        return EMPTY_STRING
    }

    fun toModel(request: CipherRequestModel): Cipher

    fun toResponse(cipher: Cipher): CipherResponseModel

    fun toResponse(cipher: Cipher, requestModel: CipherRequestModel): CipherResponseModel {
        val resp = toResponse(cipher)
        resp.fields = requestModel.fields
        resp.login = requestModel.login
        resp.identity = requestModel.identity
        resp.card = requestModel.card
        resp.secureNote = requestModel.secureNote
        resp.xObject = "cipher"
        return resp
    }

    @Mappings(
        Mapping(target = "xyObject", constant = "cipherDetails"),
        Mapping(target = "data", ignore = true),
        Mapping(target = "edit", constant = "true"),
        Mapping(target = "viewPassword", constant = "true"),
        Mapping(target = "revisionDate", source = "updatedAt")
    )
    fun toCipherDetailResponse(cipher: Cipher): CipherDetailsResponseModel
}
