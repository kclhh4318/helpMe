package com.example.helpme.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProjectDetail(
    var proj_id: Int,
    val title: String,
    var start_d: String?,
    var end_d: String?,
    var lan: String?,
    var type: String?,
    @SerializedName("user_id") var email: String,
    var contents: String?,
    var remember: String?,
    var ref: String?,
    var isLiked: Boolean?,
    var likes: Int?// 좋아요 여부
) : Parcelable
