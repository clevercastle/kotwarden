package org.clevercastle.kotwarden.web.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class IdentityConnectData(
    val grantType: String,
    val refreshToken: String? = null,
    val clientId: String? = null,
    val clientSecret: String? = null,
    val password: String? = null,
    val scope: String? = null,
    val username: String? = null,
    val deviceIdentifier: String? = null,
    val deviceName: String? = null,
    val deviceType: String? = null,
    val devicePushToken: String? = null,
    val twoFactorProvider: Int? = null,
    val twoFactorToken: String? = null,
    val twoFactorRemember: Int? = null
)


@kotlinx.serialization.Serializable
data class LoginResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    @SerialName("token_type")
    val tokenType: String = "Bearer",
    @SerialName("refresh_token")
    val refreshToken: String?,
    @SerialName("Key")
    val key: String?,
    @SerialName("PrivateKey")
    val privateKey: String? = null,
    @SerialName("Kdf")
    val kdf: Int?,
    @SerialName("KdfIterations")
    val kdfIterations: Int?,
    @SerialName("ForcePasswordReset")
    val forcePasswordReset: Boolean = false,
    @SerialName("ResetMasterPassword")
    val resetMasterPassword: Boolean = false,
    val scope: String?,
    val unofficialServer: Boolean = true
)