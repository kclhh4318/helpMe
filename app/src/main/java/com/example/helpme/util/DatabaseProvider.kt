package com.example.helpme.util

import android.content.Context
import androidx.room.Room
import com.example.helpme.data.database.AppDatabase

object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "user_database"
            ).build()
        }
        return instance!!
    }
}
