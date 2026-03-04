package com.davi.dev.scannermlkit.domain.model

import android.net.Uri

data class PdfFile(
    val name: String,
    val uri: Uri,
    val path: String? = null,
    val size: Long,
    val dateAdded: Long? = null
)
