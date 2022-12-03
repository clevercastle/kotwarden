package org.clevercastle.kotwarden.web.model

@kotlinx.serialization.Serializable
data class Permissions(
    val accessEventLogs: Boolean? = null,
    val accessImportExport: Boolean? = null,
    val accessReports: Boolean? = null,
    val manageAllCollections: Boolean? = null,
    val createNewCollections: Boolean? = null,
    val editAnyCollection: Boolean? = null,
    val deleteAnyCollection: Boolean? = null,
    val manageAssignedCollections: Boolean? = null,
    val editAssignedCollections: Boolean? = null,
    val deleteAssignedCollections: Boolean? = null,
    val manageGroups: Boolean? = null,
    val managePolicies: Boolean? = null,
    val manageSso: Boolean? = null,
    val manageUsers: Boolean? = null,
    val manageResetPassword: Boolean? = null
) 

