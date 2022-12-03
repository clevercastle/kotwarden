package org.clevercastle.kotwarden.web.model

import kotlinx.serialization.SerialName

typealias PlanType = Int


@kotlinx.serialization.Serializable
data class OrganizationCreateRequestModel(
    val name: String,
    val billingEmail: String,
    val key: String,
    val businessName: String? = null,
    val planType: PlanType? = null,
    val keys: OrganizationKeysRequestModel? = null,
    val collectionName: String
)

@kotlinx.serialization.Serializable
data class OrganizationUpdateRequestModel(
    val name: String? = null,
    val billingEmail: String? = null,
    val key: String? = null,
    val businessName: String? = null,
    val planType: PlanType? = null,
    val keys: OrganizationKeysRequestModel? = null,
    val collectionName: String? = null
)


@kotlinx.serialization.Serializable
data class OrganizationKeysRequestModel(
    val encryptedPrivateKey: String,
    val publicKey: String
)

@kotlinx.serialization.Serializable
data class CollectionRequestModel(
    val name: String,
    val externalId: String? = null,
    val groups: List<SelectionReadOnlyRequestModel>? = null
)

@kotlinx.serialization.Serializable
data class SelectionReadOnlyRequestModel(
    val id: String,
    val readOnly: Boolean? = null,
    val hidePasswords: Boolean? = null
)

@kotlinx.serialization.Serializable
data class CipherCollectionsRequestModel(
    val collectionIds: List<String>
)

@kotlinx.serialization.Serializable
data class OrganizationResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val id: String? = null,
    val identifier: String? = null,
    val name: String? = null,
    val businessName: String? = null,
    val businessAddress1: String? = null,
    val businessAddress2: String? = null,
    val businessAddress3: String? = null,
    val businessCountry: String? = null,
    val businessTaxNumber: String? = null,
    val billingEmail: String? = null,
    val plan: PlanResponseModel? = null,
    val planType: PlanType? = null,
    val seats: Int? = null,
    val maxAutoscaleSeats: Int? = null,
    val maxCollections: Int? = null,
    val maxStorageGb: Int? = null,
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
    var usersGetPremium: Boolean? = null,
    val selfHost: Boolean? = null,
    val hasPublicAndPrivateKeys: Boolean? = null
)

@kotlinx.serialization.Serializable
data class PlanResponseModel(
    val `object`: String? = null,
    val type: PlanType? = null,
    val product: ProductType? = null,
    val name: String? = null,
    val isAnnual: Boolean? = null,
    val nameLocalizationKey: String? = null,
    val descriptionLocalizationKey: String? = null,
    val canBeUsedByBusiness: Boolean? = null,
    val baseSeats: Int? = null,
    val baseStorageGb: Int? = null,
    val maxCollections: Int? = null,
    val maxUsers: Int? = null,
    val hasAdditionalSeatsOption: Boolean? = null,
    val maxAdditionalSeats: Int? = null,
    val hasAdditionalStorageOption: Boolean? = null,
    val maxAdditionalStorage: Int? = null,
    val hasPremiumAccessOption: Boolean? = null,
    val trialPeriodDays: Int? = null,
    val hasSelfHost: Boolean? = null,
    val hasPolicies: Boolean? = null,
    val hasGroups: Boolean? = null,
    val hasDirectory: Boolean? = null,
    val hasEvents: Boolean? = null,
    val hasTotp: Boolean? = null,
    val has2fa: Boolean? = null,
    val hasApi: Boolean? = null,
    val hasSso: Boolean? = null,
    val hasResetPassword: Boolean? = null,
    val usersGetPremium: Boolean? = null,
    val upgradeSortOrder: Int? = -1,
    val displaySortOrder: Int? = -1,
    val legacyYear: Int? = null,
    val disabled: Boolean? = null,
    val stripePlanId: String? = null,
    val stripeSeatPlanId: String? = null,
    val stripeStoragePlanId: String? = null,
    val stripePremiumAccessPlanId: String? = null,
    val basePrice: Double? = 0.0,
    val seatPrice: Double? = 0.0,
    val additionalStoragePricePerGb: Double? = 0.0,
    val premiumAccessOptionPrice: Double? = 0.0
)

@kotlinx.serialization.Serializable
data class CipherDetailsResponseModelListResponseModel(
    @SerialName("object")
    var xyObject: String? = null,
    var data: List<CipherDetailsResponseModel>? = null,
    var continuationToken: String? = null
)

@kotlinx.serialization.Serializable
data class OrganizationUserBulkResponseModelListResponseModel(
    @SerialName("object")
    var xyObject: String? = null,
    var data: List<OrganizationUserResponseModel>? = null,
    var continuationToken: String? = null
)

@kotlinx.serialization.Serializable
data class OrganizationUserResponseModel(
    @SerialName("object")
    var xyObject: String? = null,
    var id: String? = null,
    var userId: String? = null,
    var email: String? = null,
    var name: String? = null,
    var type: OrganizationUserType? = null,
    var status: OrganizationUserStatusType? = null,
    var accessAll: Boolean? = null,
)

@kotlinx.serialization.Serializable
data class CollectionDetailsResponseModelListResponseModel(
    @SerialName("object")
    var xyObject: String? = null,
    var data: List<CollectionDetailsResponseModel>? = null,
    var continuationToken: String? = null
)