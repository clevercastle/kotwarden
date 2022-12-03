package org.clevercastle.kotwarden.model

import org.clevercastle.kotwarden.util.COLLECTION_PREFIX
import org.clevercastle.kotwarden.web.model.CollectionDetailsResponseModel
import org.clevercastle.kotwarden.web.model.CollectionRequestModel
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.time.OffsetDateTime
import java.util.*

@DynamoDbBean
class VaultCollection {

    companion object {
        const val TABLE_NAME = "resource"
        const val SK_INDEX = "SK-Index"
        val converter: VaultCollectionConverter = Mappers.getMapper(VaultCollectionConverter::class.java)
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    lateinit var organizationId: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    @get:DynamoDbSecondaryPartitionKey(indexNames = [SK_INDEX])
    lateinit var id: String

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime


    @get:DynamoDbAttribute("Name")
    lateinit var name: String
}

@Mapper
interface VaultCollectionConverter {
    @Mappings(
        Mapping(target = "xyObject", constant = "collection"),
    )
    fun toResponse(vaultCollection: VaultCollection): CollectionDetailsResponseModel

    fun toModel(organizationId: String, request: CollectionRequestModel): VaultCollection {
        val collection = VaultCollection()
        collection.id = COLLECTION_PREFIX + UUID.randomUUID().toString()
        collection.organizationId = organizationId
        collection.name = request.name
        collection.createdAt = OffsetDateTime.now()
        collection.updatedAt = OffsetDateTime.now()
        return collection
    }


}