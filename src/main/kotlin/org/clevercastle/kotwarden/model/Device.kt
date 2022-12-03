package org.clevercastle.kotwarden.model

import org.clevercastle.kotwarden.Config
import org.clevercastle.kotwarden.web.model.RegisterRequest
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.time.OffsetDateTime

@DynamoDbBean
class Device {
    companion object {
        const val TABLE_NAME = "device"
        const val REFRESH_TOKEN_INDEX = "RefreshToken-Index"
        val converter: DeviceConverter = Mappers.getMapper(DeviceConverter::class.java)
    }

    constructor()

    constructor(id: String, userId: String, name: String, type: DeviceType) {
        val now = OffsetDateTime.now()
        this.id = id
        this.userId = userId
        this.createdAt = now
        this.updatedAt = now
        this.name = name
        this.type = type
        this.pushToken = null
        this.refreshToken = ""
        this.twoFactorRemember = null
    }

    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("Id")
    lateinit var id: String

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("UserId")
    lateinit var userId: String

    @get:DynamoDbAttribute("Name")
    lateinit var name: String

    @get:DynamoDbAttribute("DeviceType")
    lateinit var type: DeviceType

    @get:DynamoDbAttribute("PushToken")
    var pushToken: String? = null

    @get:DynamoDbSecondaryPartitionKey(indexNames = [REFRESH_TOKEN_INDEX])
    @get:DynamoDbAttribute("RefreshToken")
    lateinit var refreshToken: String

    @get:DynamoDbAttribute("TwoFactorRemember")
    var twoFactorRemember: String? = null

    @get:DynamoDbAttribute("TTL")
    var ttl: Long = OffsetDateTime.now().toEpochSecond() + Config.config.deviceTtl

    @get:DynamoDbAttribute("CreatedAt")
    lateinit var createdAt: OffsetDateTime

    @get:DynamoDbAttribute("UpdatedAt")
    lateinit var updatedAt: OffsetDateTime
}

enum class DeviceType(val value: Int) {
    Android(0), iOS(1), ChromeExtension(2), FirefoxExtension(3), OperaExtension(4), EdgeExtension(5), WindowsDesktop(6), MacOsDesktop(
        7
    ),
    LinuxDesktop(8), ChromeBrowser(9), FirefoxBrowser(10), OperaBrowser(11), EdgeBrowser(12), IEBrowser(13), UnknownBrowser(
        14
    ),
    AndroidAmazon(15), UWP(16), SafariBrowser(17), VivaldiBrowser(18), VivaldiExtension(19), SafariExtension(20);

    companion object {
        fun parse(type: String?): DeviceType {
            return if (type == null) {
                UnknownBrowser
            } else {
                val list = DeviceType.values().filter { it.value == type.toInt() }.toList()
                if (list.isEmpty()) {
                    UnknownBrowser
                } else {
                    list[0]
                }
            }
        }
    }
}

@Mapper
interface DeviceConverter {
    /**
     * missing required property: id
     */
    @Mappings(
        Mapping(source = "keys.encryptedPrivateKey", target = "encryptedPrivateKey"),
        Mapping(source = "keys.publicKey", target = "publicKey")
    )
    fun toModel(registerReq: RegisterRequest): User
}