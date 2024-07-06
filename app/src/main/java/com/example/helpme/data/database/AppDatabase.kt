package com.example.helpme.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.helpme.data.entity.User
import com.example.helpme.data.dao.UserDao

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
