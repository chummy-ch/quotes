package com.example.quotes.quotedatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QuoteEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun quoteDao(): QuoteDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDataBase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "quote_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
