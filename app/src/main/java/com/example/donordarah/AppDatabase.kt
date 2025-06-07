package com.example.donordarah

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pendonor::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pendonorDao(): PendonorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pendonor_database"
                ).allowMainThreadQueries() // Untuk pemula: supaya bisa query langsung di UI Thread
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
