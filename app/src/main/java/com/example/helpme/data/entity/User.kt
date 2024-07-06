package com.example.helpme.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")

data class User(
    @PrimaryKey val id: Long,
    val nickname: String?,
    val email: String?
)
