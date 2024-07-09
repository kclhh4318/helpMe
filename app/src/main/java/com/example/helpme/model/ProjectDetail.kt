package com.example.helpme.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProjectDetail(
    val proj_id: Int,
    val title: String,
    var start_d: String?,
    var end_d: String?,
    var lang: String?,
    var type: String,
    var email: String,
    var contents: String,
    var remember: String,
    var ref: String,
    var isLiked: Boolean,
    var likes: Int
) : Parcelable