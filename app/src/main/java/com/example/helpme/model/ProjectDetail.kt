package com.example.helpme.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProjectDetail(
    var proj_id: Int,
    val title: String,
    var start_d: String?,
    var end_d: String?,
    var lan: String?,
    var type: String?,
    var email: String?,
    var contents: String?,
    var remember: String?,
    var ref: String?,
    var likes: Boolean?,
) : Parcelable
