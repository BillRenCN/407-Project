package com.cs407.project.data

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Review::class], version = 2, exportSchema = false)
abstract class ReviewDatabase : RoomDatabase() {

    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: ReviewDatabase? = null

        // Migration strategy from version 1 to version 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example migration logic: Adding a new column to the review table
                database.execSQL("ALTER TABLE review_table ADD COLUMN new_column_name TEXT DEFAULT ''")
            }
        }

        // Get the database instance
        fun getDatabase(context: Context): ReviewDatabase {
            // Check for an existing instance
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                    Log.d("ReviewDatabase", "New database instance created")
                }
            }
        }

        // Build the database instance
        private fun buildDatabase(context: Context): ReviewDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ReviewDatabase::class.java,
                "review_database"
            )
                .addMigrations(MIGRATION_1_2) // Add migration strategy
                .fallbackToDestructiveMigration() // Optional: Allow destructive migrations
                .build()
        }
    }
}
