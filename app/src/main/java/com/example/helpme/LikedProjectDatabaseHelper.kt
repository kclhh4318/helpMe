package com.example.helpme

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LikedProjectsDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PROJECT_ID TEXT," +
                "$COLUMN_USER_EMAIL TEXT," +
                "UNIQUE ($COLUMN_PROJECT_ID, $COLUMN_USER_EMAIL) ON CONFLICT REPLACE)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "likedProjects.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "likedProjects"
        const val COLUMN_ID = "id"
        const val COLUMN_PROJECT_ID = "projectId"
        const val COLUMN_USER_EMAIL = "userEmail"
    }
}
