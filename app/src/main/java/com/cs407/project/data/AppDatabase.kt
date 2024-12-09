package com.cs407.project.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Second database: Users
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UsersDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UsersDatabase? = null

        fun getDatabase(context: Context): UsersDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UsersDatabase::class.java,
                    "users_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Third database: Messages
@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class MessagesDatabase : RoomDatabase() {
    abstract fun messagesDao(): MessagesDao

    companion object {
        @Volatile
        private var INSTANCE: MessagesDatabase? = null

        fun getDatabase(context: Context): MessagesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessagesDatabase::class.java,
                    "messages_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Database(entities = [Review::class], version = 1, exportSchema = false)
abstract class ReviewDatabase : RoomDatabase() {
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

