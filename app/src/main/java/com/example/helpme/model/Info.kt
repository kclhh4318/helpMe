package com.example.helpme.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Info (
    val end_d_null_count: Int = 0,
    val end_d_not_null_count: Int = 0,
    val total_likes: Int = 0,
    val most_common_lan: String = "Unknown",
    val most_common_type: String = "Unknown"
): Parcelable