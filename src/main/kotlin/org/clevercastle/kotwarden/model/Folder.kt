package org.clevercastle.kotwarden.model

import org.clevercastle.kotwarden.web.model.FolderResponseModel
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.OffsetDateTime

@DynamoDbBean
class Folder {

    companion object {
        const val TABLE_NAME = "resource"
        val converter: FolderConverter = Mappers.getMapper(FolderConverter::class.java)
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    lateinit var userId: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    lateinit var id: String

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime


    @get:DynamoDbAttribute("Name")
    lateinit var name: String
}

@Mapper
interface FolderConverter {
    @Mappings(
        Mapping(target = "xyObject", constant = "folder"), Mapping(target = "revisionDate", source = "updatedAt")
    )
    fun toFolderResponse(folder: Folder): FolderResponseModel
}