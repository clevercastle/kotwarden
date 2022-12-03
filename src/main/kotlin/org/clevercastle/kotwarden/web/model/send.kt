package org.clevercastle.kotwarden.web.model


typealias SendType = Int

data class SendFileModel(
    val id: String? = null,
    val fileName: String? = null,
    val size: Long? = null,
    val sizeName: String? = null
)

data class SendTextModel(
    val text: String? = null,
    val hidden: Boolean? = null
)