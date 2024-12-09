package com.cs407.project.data

import androidx.room.*
import android.content.Context

@Database(entities = [Review::class], version = 1)
abstract class ReviewDatabase : RoomDatabase() {
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: ReviewDatabase? = null

        fun getDatabase(context: Context): ReviewDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReviewDatabase::class.java,
                    "review_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
