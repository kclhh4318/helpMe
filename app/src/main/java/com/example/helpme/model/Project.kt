package com.example.helpme.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Project(
    var proj_id: Int,
    var title: String,
    var start_d: String,
    var end_d: String?,
    var lang: String?,
    var type: String,
    var email: String
) : Parcelable