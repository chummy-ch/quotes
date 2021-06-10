package com.example.quotes.quotedatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuoteEntity(
    @PrimaryKey val uid: Int,
    @ColumnInfo val quote: String,
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean
)
