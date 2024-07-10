package com.example.helpme.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Project(
    var proj_id: Int,
    var title: String,
    var start_d: String?,
    var end_d: String?,
    var lan: String? = null,
    var type: String,
    @SerializedName("user_id") var email: String
) : Parcelable
