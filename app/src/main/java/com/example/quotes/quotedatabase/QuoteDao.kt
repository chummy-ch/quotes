package com.example.quotes.quotedatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuoteDao {
    @Insert
    fun insertAll(vararg quoteEntities: QuoteEntity)

    @Delete
    fun delete(quoteEntity: QuoteEntity)

    @Query("select * from quote")
    fun getAll(): List<QuoteEntity>

    @Query("select is_favorite from quote where uid like :quoteId")
    fun isFavorite(quoteId: Int): Boolean
}
