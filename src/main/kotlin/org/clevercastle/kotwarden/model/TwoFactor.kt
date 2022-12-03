package org.clevercastle.kotwarden.model

import org.clevercastle.kotwarden.web.model.TwoFactorProviderResponseModel
import org.mapstruct.InjectionStrategy
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
class TwoFactor {
    companion object {
        const val TABLE_NAME = "resource"
        val converter: TwoFactorConverter = Mappers.getMapper(TwoFactorConverter::class.java)
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    lateinit var userId: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    lateinit var id: String

    @get:DynamoDbAttribute("Type")
    var type: Int = -1

    @get:DynamoDbAttribute("Enabled")
    var enabled: Boolean = false

    @get:DynamoDbAttribute("Data")
    lateinit var data: String

    @get:DynamoDbAttribute("LastUsed")
    var lastUsed: Int = -1

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime
}

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@JvmDefaultWithCompatibility
interface TwoFactorConverter {

    @Mappings(
        Mapping(target = "xyObject", constant = "twoFactorProvider"),
    )
    fun toProviderResponse(twoFactor: TwoFactor): TwoFactorProviderResponseModel
}

