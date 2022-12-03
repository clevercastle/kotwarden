package org.clevercastle.kotwarden.web.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.clevercastle.kotwarden.Config
import org.clevercastle.kotwarden.model.Device
import org.clevercastle.kotwarden.model.User
import org.clevercastle.kotwarden.util.convert
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import java.time.OffsetDateTime
import java.util.Base64
import java.util.Date
import kotlin.random.Random

interface IDeviceRepository {
    fun findByIdAndUser(id: String, userId: String): Device?
    fun findByRefreshToken(refreshToken: String): Device?
    fun deleteAllByUser(userId: String)
    fun save(device: Device)
}

class DeviceRepository(private val client: DynamoDbEnhancedClient) : IDeviceRepository {
    private val schema = TableSchema.fromBean(Device::class.java)
    private val table = client.table(Device.TABLE_NAME, schema)

    override fun findByIdAndUser(id: String, userId: String): Device? {
        val key = Key.builder().partitionValue(id).sortValue(userId).build()
        return table.getItem(key)
    }

    override fun findByRefreshToken(refreshToken: String): Device? {
        val queryConditional = QueryConditional
            .keyEqualTo(Key.builder().partitionValue(refreshToken).build())
        val idx = table.index(Device.REFRESH_TOKEN_INDEX)
        val iter = idx.query(
            QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build()
        )
        val list = convert(iter)
        return if (list.isNotEmpty()) {
            list[0]
        } else {
            null
        }
    }

    override fun deleteAllByUser(userId: String) {
        TODO("Not yet implemented")
    }

    override fun save(device: Device) {
        return table.putItem(device)
    }
}

class DeviceService(private val deviceRepository: DeviceRepository) {
    fun findByIdAndUser(id: String, userId: String): Device? {
        return deviceRepository.findByIdAndUser(id, userId)
    }

    fun findByRefreshToken(refreshToken: String): Device? {
        return deviceRepository.findByRefreshToken(refreshToken)
    }

    fun save(device: Device) {
        return deviceRepository.save(device)
    }

    fun refreshToken(device: Device, user: User, scope: List<String>): Pair<String, Long> {
        // TODO: 2022/6/26 always refresh token 
        if (device.refreshToken.isEmpty()) {
            device.refreshToken = Base64.getEncoder().encodeToString(Random.Default.nextBytes(64))
        }
        device.updatedAt = OffsetDateTime.now()

        //@formatter:off
        return JWT.create()
            .withNotBefore(Date(OffsetDateTime.now().toInstant().toEpochMilli()))
            .withExpiresAt(
                Date(
                    OffsetDateTime.now().plusHours(Config.config.defaultValidityHours).toInstant().toEpochMilli()
                )
            )
            .withAudience(Config.config.jwtAudience)
            .withIssuer(Config.config.jwtIssuer)
            .withSubject(user.id)
            .withClaim("premium", true)
            .withClaim("id", user.id)
            .withClaim("name", user.name)
            .withClaim("email", user.email)
            .withClaim("email_verified", true)
            .withClaim("orgowner", mutableListOf<String>())
            .withClaim("orgadmin", mutableListOf<String>())
            .withClaim("orguser", mutableListOf<String>())
            .withClaim("orgmanager", mutableListOf<String>())
            .withClaim("device", device.id)
            .withClaim("scope", scope)
            .withClaim("amr", mutableListOf("Application"))
            .sign(
                Algorithm.RSA256(
                    Config.getPublicKey(),
                    Config.getPrivateKey()
                )
            ) to Config.config.defaultValidityHours * 3600L
        //@formatter:on
    }
}