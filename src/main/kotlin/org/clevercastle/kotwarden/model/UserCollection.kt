package org.clevercastle.kotwarden.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
class UserCollection {
    companion object {
        const val TABLE_NAME = "user_collection"
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("UserId")
    lateinit var userId: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("CollectionId")
    lateinit var collectionId: String

    @get:DynamoDbAttribute("ReadOnly")
    var readOnly = false

    @get:DynamoDbAttribute("HidePasswords")
    var hidePasswords = true
}