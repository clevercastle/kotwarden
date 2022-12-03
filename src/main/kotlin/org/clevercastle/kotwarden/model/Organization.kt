package org.clevercastle.kotwarden.model

import org.clevercastle.kotwarden.util.ORGANIZATION_PREFIX
import org.clevercastle.kotwarden.util.isNotEmpty
import org.clevercastle.kotwarden.web.model.*
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.OffsetDateTime
import java.util.*

@DynamoDbBean
class Organization {
    companion object {
        const val TABLE_NAME = "resource"
        val converter: OrganizationConverter = Mappers.getMapper(OrganizationConverter::class.java)
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    lateinit var id: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    lateinit var sk: String

    @get:DynamoDbAttribute("Key")
    var key: String? = null

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime

    @get:DynamoDbAttribute("Name")
    lateinit var name: String

    @get:DynamoDbAttribute("BusinessName")
    var businessName: String? = null

    @get:DynamoDbAttribute("BillingEmail")
    lateinit var billingEmail: String

    @get:DynamoDbAttribute("PrivateKey")
    var encryptedPrivateKey: String? = null

    @get:DynamoDbAttribute("PublicKey")
    var publicKey: String? = null

    @get:DynamoDbAttribute("PlanType")
    var planType: PlanType? = null
}

@Mapper
interface OrganizationConverter {
    @Mappings(
        Mapping(target = "xyObject", constant = "organization"),
        Mapping(target = "planType", constant = "5"),
        Mapping(target = "useTotp", constant = "true")
    )
    fun toResponse(organization: Organization): OrganizationResponseModel

    @Mappings(
        Mapping(target = "xyObject", constant = "profileOrganization"),
        Mapping(target = "seats", constant = "100"),
        Mapping(target = "maxCollections", constant = "100"),
        Mapping(target = "useTotp", constant = "true"),
        Mapping(target = "usersGetPremium", constant = "true")
    )
    fun toProfileResponse(organization: Organization): ProfileOrganizationResponseModel

    @Mappings(
        Mapping(source = "keys.encryptedPrivateKey", target = "encryptedPrivateKey"),
        Mapping(source = "keys.publicKey", target = "publicKey"),
        Mapping(target = "id", expression = "java(id())"),
        Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now())"),
        Mapping(target = "updatedAt", expression = "java(java.time.OffsetDateTime.now())")
    )
    fun toModel(request: OrganizationCreateRequestModel): Organization


    fun toModel(request: OrganizationUpdateRequestModel, organization: Organization): Organization {
        // TODO: 2022/7/25 deep copy?
        if (request.keys != null) {
            organization.publicKey = request.keys.publicKey
            organization.encryptedPrivateKey = request.keys.encryptedPrivateKey
        }
        if (isNotEmpty(request.key)) {
            organization.key = request.key!!
        }
        if (isNotEmpty(request.billingEmail)) {
            organization.billingEmail = request.billingEmail!!
        }
        if (isNotEmpty(request.name)) {
            organization.name = request.name!!
        }
        if (isNotEmpty(request.businessName)) {
            organization.businessName = request.name!!
        }
        if (request.planType != null) {
            organization.planType = request.planType
        }
        organization.updatedAt = OffsetDateTime.now()
        return organization
    }

    fun id(): String {
        return ORGANIZATION_PREFIX + UUID.randomUUID().toString()
    }
}