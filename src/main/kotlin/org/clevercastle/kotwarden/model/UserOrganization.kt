package org.clevercastle.kotwarden.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.time.OffsetDateTime

//Owner:0 ; Admin:1 ; User:2 ; Manager:3
typealias  UserOrganizationType = Int
//Invited:0 ; Accepted:1 ; Confirmed:2
typealias  UserOrganizationStatus = Int

@DynamoDbBean
class UserOrganization {
    class Type {
        companion object {
            const val Owner = 0
            const val Admin = 1
            const val User = 2
            const val Manager = 3
        }
    }

    class Status {
        companion object {
            const val Invited = 0
            const val Accepted = 1
            const val Confirmed = 2
        }
    }

    companion object {
        const val TABLE_NAME = "user_organization"
        const val REVERSE_INDEX = "Reverse-Index"
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbSecondarySortKey(indexNames = [REVERSE_INDEX])
    @get:DynamoDbAttribute("UserId")
    lateinit var userId: String


    @get:DynamoDbSortKey
    @get:DynamoDbSecondaryPartitionKey(indexNames = [REVERSE_INDEX])
    @get:DynamoDbAttribute("OrganizationId")
    lateinit var organizationId: String

    @get:DynamoDbAttribute("AccessAll")
    var accessAll: Boolean = false

    @get:DynamoDbAttribute("Type")
    var type: UserOrganizationType = -1

    @get:DynamoDbAttribute("Status")
    var status: UserOrganizationStatus = -1

    @get:DynamoDbAttribute("Key")
    var key: String? = null

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime


}