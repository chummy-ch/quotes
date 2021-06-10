package com.example.quotes.quote

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuoteModel(
    val id: Long,
    val text: String,
    val categoryId: Long,
    val isFavorite: Boolean,
    val isViewed: Boolean
) : Parcelable
