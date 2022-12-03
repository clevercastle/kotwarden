package org.clevercastle.kotwarden.web.model

import kotlinx.serialization.SerialName
import org.clevercastle.kotwarden.model.serializer.OffsetDatetimeNullableSerializer
import java.time.OffsetDateTime

@kotlinx.serialization.Serializable
data class FolderRequestModel(
    val name: String
)

@kotlinx.serialization.Serializable
data class FolderResponseModel(
    @SerialName("object")
    val xyObject: String? = null,
    val id: String? = null,
    val name: String? = null,
    @kotlinx.serialization.Serializable(with = OffsetDatetimeNullableSerializer::class)
    val revisionDate: OffsetDateTime? = null
)