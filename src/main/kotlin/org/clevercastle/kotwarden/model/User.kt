package org.clevercastle.kotwarden.model

import org.clevercastle.kotwarden.Config
import org.clevercastle.kotwarden.web.model.ProfileResponseModel
import org.clevercastle.kotwarden.web.model.RegisterRequest
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.security.SecureRandom

@DynamoDbBean
class User {
    companion object {
        const val TABLE_NAME = "resource"
        const val Email_INDEX = "Email-Index"
        val converter: UserConverter = Mappers.getMapper(UserConverter::class.java)
    }

    constructor()

    constructor(email: String, id: String, masterPasswordHash: String?) {
        this.email = email
        this.id = id
        this.masterPasswordHint = masterPasswordHash
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    lateinit var id: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    lateinit var sk: String


    @get:DynamoDbSecondaryPartitionKey(indexNames = [Email_INDEX])
    @get:DynamoDbAttribute("Email")
    lateinit var email: String

    @get:DynamoDbAttribute("Name")
    var name: String? = null

    @get:DynamoDbAttribute("Token")
    var token: String? = null

    @get:DynamoDbAttribute("Key")
    var key: String? = null

    @get:DynamoDbAttribute("PrivateKey")
    var encryptedPrivateKey: String? = null

    @get:DynamoDbAttribute("PublicKey")
    var publicKey: String? = null

    @get:DynamoDbAttribute("MasterPasswordHint")
    var masterPasswordHint: String? = null

    @get:DynamoDbAttribute("MasterPasswordHash")
    var masterPasswordHash: String? = null

    @get:DynamoDbAttribute("Salt")
    var salt: ByteArray = ByteArray(32)

    @get:DynamoDbAttribute("Kdf")
    var kdf: Int = Config.config.kdf

    @get:DynamoDbAttribute("KdfIterations")
    var kdfIterations: Int = Config.config.kdfIterations


    // TODO: 2022/6/21 default from config 
    @get:DynamoDbAttribute("Enabled")
    var enabled: Boolean = true

    // infer whether client should re-sign in
    @get:DynamoDbAttribute("SecurityStamp")
    var securityStamp: String? = null

    init {
        SecureRandom().nextBytes(this.salt)
    }
}

@Mapper
interface UserConverter {
    /**
     * missing required property: id
     */
    @Mappings(
        Mapping(source = "keys.encryptedPrivateKey", target = "encryptedPrivateKey"),
        Mapping(source = "keys.publicKey", target = "publicKey")
    )
    fun toModel(registerReq: RegisterRequest): User


    @Mappings(
        Mapping(target = "xyObject", constant = "profile"),
        Mapping(target = "premium", constant = "true"),
        Mapping(target = "premiumFromOrganization", constant = "true"),
        Mapping(target = "privateKey", source = "encryptedPrivateKey")
    )
    fun toProfileResponse(user: User): ProfileResponseModel
}