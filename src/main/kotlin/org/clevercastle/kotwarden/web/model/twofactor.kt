package org.clevercastle.kotwarden.web.model

import kotlinx.serialization.SerialName

typealias TwoFactorProviderType = Int

@kotlinx.serialization.Serializable
data class TwoFactorProviderResponseModelListResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val data: List<TwoFactorProviderResponseModel>? = null,
    val continuationToken: String? = null
)

@kotlinx.serialization.Serializable
data class TwoFactorProviderResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val enabled: Boolean? = null,
    val type: TwoFactorProviderType? = null
)

