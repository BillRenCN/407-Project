package com.cs407.project.data


import android.content.Context
import androidx.room.Room

object ReviewDataHelper {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    /**
     * Get the instance of the AppDatabase.
     * If the instance does not exist, it will be created.
     */
    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database" // Database name
            ).build()
            INSTANCE = instance
            instance
        }
    }

    /**
     * Clear the database instance.
     * Useful for testing or resetting the app's state.
     */
    fun clearDatabase() {
        INSTANCE = null
    }
}
