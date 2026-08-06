package com.example.koreanwebtoonsentenceminer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Translation::class, KoreanWordFts::class], 
    version = 1, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dictionaryDao(): DictionaryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                
                val instance = Room.databaseBuilder(
                    context.applicationContext, //prevent accidentally leaking an Activity context
                    AppDatabase::class.java,
                    "korean_mining_db"
                ).build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}