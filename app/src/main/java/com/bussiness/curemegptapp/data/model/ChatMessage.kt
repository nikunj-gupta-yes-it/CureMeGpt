package com.bussiness.curemegptapp.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
data class PdfData(
    val uri: Uri,
    val name: String
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String? = null,
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val images: List<Uri> = emptyList(),
    val pdfs: List<PdfData> = emptyList()
)

 */

@Parcelize
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String? = null,
    val isUser: Boolean = false,
    val images: List<Uri> = emptyList(),
    val pdfs: List<PdfData> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val isGenerating: Boolean = false,
    val rating: Int = 0,       // 1 = like, -1 = dislike, 0 = none
    val isRated: Boolean = false
) :  Parcelable

@Parcelize
data class PdfData(
    val uri: Uri,
    val name: String
) : Parcelable



