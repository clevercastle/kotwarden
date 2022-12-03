package org.clevercastle.kotwarden.web.model

import kotlinx.serialization.SerialName
import org.clevercastle.kotwarden.model.serializer.OffsetDatetimeNullableSerializer
import java.time.OffsetDateTime

typealias OrganizationUserStatusType = Int
typealias OrganizationUserType = Int

@kotlinx.serialization.Serializable
data class ProfileResponseModel(
    @SerialName("_status")
    private val status: Int = 0,
    @SerialName("object")
    val xyObject: String? = null,
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    var emailVerified: Boolean? = null,
    val premium: Boolean? = null,
    val premiumFromOrganization: Boolean? = null,
    val masterPasswordHint: String? = null,
    val culture: String? = null,
    val twoFactorEnabled: Boolean? = null,
    val key: String? = null,
    val privateKey: String? = null,
    val securityStamp: String? = null,
    val forcePasswordReset: Boolean? = null,
    val usesKeyConnector: Boolean? = null,
    var organizations: List<ProfileOrganizationResponseModel>? = null,
    val providers: List<ProfileProviderResponseModel>? = null,
    val providerOrganizations: List<ProfileProviderOrganizationResponseModel>? = null
)

@kotlinx.serialization.Serializable
data class ProfileProviderResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val id: String? = null,
    val name: String? = null,
    val key: String? = null,
    val status: ProviderUserStatusType? = null,
    val type: ProviderUserType? = null,
    val enabled: Boolean? = null,
    val permissions: Permissions? = null,
    val userId: String? = null,
    val useEvents: Boolean? = null
)

@kotlinx.serialization.Serializable
data class ProfileProviderOrganizationResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val id: String? = null,
    val name: String? = null,
    val usePolicies: Boolean? = null,
    val useSso: Boolean? = null,
    val useKeyConnector: Boolean? = null,
    val useGroups: Boolean? = null,
    val useDirectory: Boolean? = null,
    val useEvents: Boolean? = null,
    val useTotp: Boolean? = null,
    val use2fa: Boolean? = null,
    val useApi: Boolean? = null,
    val useResetPassword: Boolean? = null,
    val usersGetPremium: Boolean? = null,
    val selfHost: Boolean? = null,
    val seats: Int? = null,
    val maxCollections: Int? = null,
    val maxStorageGb: Int? = null,
    val key: String? = null,
    val status: OrganizationUserStatusType? = null,
    val type: OrganizationUserType? = null,
    val enabled: Boolean? = null,
    val ssoBound: Boolean? = null,
    val identifier: String? = null,
    val permissions: Permissions? = null,
    val resetPasswordEnrolled: Boolean? = null,
    val userId: String? = null,
    val hasPublicAndPrivateKeys: Boolean? = null,
    val providerId: String? = null,
    val providerName: String? = null,
    val familySponsorshipFriendlyName: String? = null,
    val familySponsorshipAvailable: Boolean? = null,
    val planProductType: ProductType? = null,
    val keyConnectorEnabled: Boolean? = null,
    val keyConnectorUrl: String? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class)
    val familySponsorshipLastSyncDate: OffsetDateTime? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class)
    val familySponsorshipValidUntil: OffsetDateTime? = null,
    val familySponsorshipToDelete: Boolean? = null
)


@kotlinx.serialization.Serializable
data class ProfileOrganizationResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val id: String? = null,
    val name: String? = null,
    val usePolicies: Boolean? = null,
    val useSso: Boolean? = null,
    val useKeyConnector: Boolean? = null,
    val useGroups: Boolean? = null,
    val useDirectory: Boolean? = null,
    val useEvents: Boolean? = null,
    val useTotp: Boolean? = null,
    val use2fa: Boolean? = null,
    val useApi: Boolean? = null,
    val useResetPassword: Boolean? = null,
    val usersGetPremium: Boolean? = null,
    val selfHost: Boolean? = null,
    val seats: Int? = null,
    val maxCollections: Int? = null,
    val maxStorageGb: Int? = null,
    var key: String? = null,
    var status: OrganizationUserStatusType? = null,
    var type: OrganizationUserType? = null,
    var enabled: Boolean? = null,
    val ssoBound: Boolean? = null,
    val identifier: String? = null,
    val permissions: Permissions? = null,
    val resetPasswordEnrolled: Boolean? = null,
    var userId: String? = null,
    var hasPublicAndPrivateKeys: Boolean? = null,
    val providerId: String? = null,
    val providerName: String? = null,
    val familySponsorshipFriendlyName: String? = null,
    val familySponsorshipAvailable: Boolean? = null,
    val planProductType: ProductType? = null,
    val keyConnectorEnabled: Boolean? = null,
    val keyConnectorUrl: String? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class)
    val familySponsorshipLastSyncDate: OffsetDateTime? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class)
    val familySponsorshipValidUntil: OffsetDateTime? = null,
    val familySponsorshipToDelete: Boolean? = null
)
