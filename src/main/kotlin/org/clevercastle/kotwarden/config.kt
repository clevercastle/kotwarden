package org.clevercastle.kotwarden

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.Application
import io.ktor.server.config.ApplicationConfig
import org.clevercastle.kotwarden.util.decodeFromString
import org.clevercastle.kotwarden.util.isEmpty
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationRequest
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse
import software.amazon.awssdk.services.appconfigdata.model.StartConfigurationSessionRequest
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

val dotenv = dotenv {
    directory = ".env"
    ignoreIfMalformed = true
    ignoreIfMissing = true
}

//1: local; 2: aws AppConfig
typealias ConfigType = Int

@kotlinx.serialization.Serializable
data class KotwardenConfig(
    val deviceTtl: Int = 120960,
    val kdf: Int = 0,
    val kdfIterations: Int = 10000,
    val defaultValidityHours: Long = 2L,
    val jwtIssuer: String = "kotwarden",
    val jwtAudience: String = "audience",
    val corsHost: String = "*",
    val jwtPrivateKey: String,
    val jwtPublicKey: String,
    val signUpAllowed: Boolean = false
)

fun loadConfig(developmentMode: Boolean, type: ConfigType, config: ApplicationConfig): KotwardenConfig {
    return if (developmentMode) {
        KotwardenConfig(
            deviceTtl = dotenv["DEVICE_TTL"]?.toInt() ?: (3600 * 24 * 14),
            kdfIterations = dotenv["KDF_ITERATIONS"]?.toInt() ?: 10000,
            defaultValidityHours = dotenv["DEFAULT_VALIDITY_HOURS"]?.toLong() ?: 2L,
            jwtIssuer = dotenv["JWT_ISSUER"] ?: "kotwarden",
            jwtAudience = dotenv["JWT_AUDIENCE"] ?: "audience",
            corsHost = dotenv["CORS_HOST"] ?: "*",
            jwtPrivateKey = dotenv["JWT_PRIVATE_KEY"]
                ?: Application::class.java.getResource("/credentials/private_key_pkcs8.pem")?.readText()
                ?: error("private key not found"),
            jwtPublicKey = dotenv["JWT_PUBLIC_KEY"]
                ?: Application::class.java.getResource("/credentials/public_key.pem")?.readText()
                ?: error("public key not found")
        )
    } else if (type == 1) {
        KotwardenConfig(
            deviceTtl = dotenv["DEVICE_TTL"]?.toInt() ?: (3600 * 24 * 14),
            kdfIterations = dotenv["KDF_ITERATIONS"]?.toInt() ?: 10000,
            defaultValidityHours = dotenv["DEFAULT_VALIDITY_HOURS"]?.toLong() ?: 2L,
            jwtIssuer = dotenv["JWT_ISSUER"] ?: "kotwarden",
            jwtAudience = dotenv["JWT_AUDIENCE"] ?: "audience",
            corsHost = dotenv["CORS_HOST"] ?: "*",
            jwtPrivateKey = dotenv["JWT_PRIVATE_KEY"] ?: "",
            jwtPublicKey = dotenv["JWT_PUBLIC_KEY"] ?: ""
        )
    } else {
        loadAppConfig(config)
    }
}

class Config {
    companion object {
        var config: KotwardenConfig = KotwardenConfig(jwtPrivateKey = "", jwtPublicKey = "")
        fun isSignupAllowed(email: String): Boolean {
            // todo dynamic check whether sign up is allowed or not
            return config.signUpAllowed
        }

        fun getPrivateKey(): RSAPrivateKey {
            return getPrivateKey(config.jwtPrivateKey) as RSAPrivateKey
        }

        fun getPublicKey(): RSAPublicKey {
            return getPublicKey(config.jwtPublicKey) as RSAPublicKey
        }
    }
}

fun getPrivateKey(privateKey: String): PrivateKey {
    isEmpty(privateKey).let {
        if (it) {
            throw IllegalArgumentException("private key is empty")
        }
    }
    val privateKeyPEM =
        privateKey.replace("-----BEGIN PRIVATE KEY-----", "").replace(System.lineSeparator().toRegex(), "")
            .replace("-----END PRIVATE KEY-----", "")
    val encoded = Base64.getDecoder().decode(privateKeyPEM)
    val spec = PKCS8EncodedKeySpec(encoded)
    val kf: KeyFactory = KeyFactory.getInstance("RSA")
    return kf.generatePrivate(spec)
}

fun getPublicKey(publicKey: String): PublicKey {
    isEmpty(publicKey).let {
        if (it) {
            throw IllegalArgumentException("public key is empty")
        }
    }
    val privateKeyPEM =
        publicKey.replace("-----BEGIN PUBLIC KEY-----", "").replace(System.lineSeparator().toRegex(), "")
            .replace("-----END PUBLIC KEY-----", "")
    val encoded = Base64.getDecoder().decode(privateKeyPEM)
    val spec = X509EncodedKeySpec(encoded)
    val kf: KeyFactory = KeyFactory.getInstance("RSA")
    return kf.generatePublic(spec)
}

private var lastToken = ""
fun loadAppConfig(config: ApplicationConfig): KotwardenConfig {
    val client = AppConfigDataClient.builder().build()
    val sessionRequest = StartConfigurationSessionRequest.builder()
        .applicationIdentifier(config.property("kotwarden.config.application").getString())
        .configurationProfileIdentifier(config.property("kotwarden.config.profile").getString())
        .environmentIdentifier(config.property("kotwarden.config.environment").getString()).build()
    val session = client.startConfigurationSession(sessionRequest)
    lastToken = session.initialConfigurationToken()

    val request = GetLatestConfigurationRequest.builder().configurationToken(lastToken).build()
    val response: GetLatestConfigurationResponse = client.getLatestConfiguration(request)
    lastToken = response.nextPollConfigurationToken()
    var content: String? = null
    if (response.configuration().asByteArray() != null && response.configuration().asByteArray().isNotEmpty()) {
        content = response.configuration().asUtf8String()
    }
    return decodeFromString<KotwardenConfig>(content!!)!!
}

