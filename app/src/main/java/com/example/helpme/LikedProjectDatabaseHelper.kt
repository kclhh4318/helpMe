package com.example.helpme

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LikedProjectsDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PROJECT_ID TEXT," +
                "$COLUMN_USER_EMAIL TEXT," +
                "$COLUMN_LIKES INTEGER DEFAULT 0," +
                "UNIQUE ($COLUMN_PROJECT_ID, $COLUMN_USER_EMAIL) ON CONFLICT REPLACE)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun saveLikedProject(projectId: String, userEmail: String) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put(COLUMN_PROJECT_ID, projectId)
                put(COLUMN_USER_EMAIL, userEmail)
            }
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        } finally {
            db.close()
        }
    }

    fun removeLikedProject(projectId: String, userEmail: String) {
        val db = this.writableDatabase
        try {
            db.delete(TABLE_NAME, "$COLUMN_PROJECT_ID = ? AND $COLUMN_USER_EMAIL = ?", arrayOf(projectId, userEmail))
        } finally {
            db.close()
        }
    }

    fun isProjectLiked(projectId: String, userEmail: String): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        return try {
            cursor = db.query(
                TABLE_NAME,
                null,
                "$COLUMN_PROJECT_ID = ? AND $COLUMN_USER_EMAIL = ?",
                arrayOf(projectId, userEmail),
                null,
                null,
                null
            )
            cursor.count > 0
        } finally {
            cursor?.close()
            db.close()
        }
    }

    fun getProjectLikes(projectId: String): Int {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        return try {
            cursor = db.query(
                TABLE_NAME,
                arrayOf("COUNT(*)"),
                "$COLUMN_PROJECT_ID = ?",
                arrayOf(projectId),
                null,
                null,
                null
            )
            var likes = 0
            if (cursor.moveToFirst()) {
                likes = cursor.getInt(0)
            }
            likes
        } finally {
            cursor?.close()
            db.close()
        }
    }

    companion object {
        const val DATABASE_NAME = "likedProjects.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "likedProjects"
        const val COLUMN_ID = "id"
        const val COLUMN_PROJECT_ID = "projectId"
        const val COLUMN_USER_EMAIL = "userEmail"
        const val COLUMN_LIKES = "likes"
    }
}
