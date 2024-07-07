package com.example.helpme.network

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Project(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("start_date") val startDate: String = "",
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("language") val language: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("contents") var contents: String = "",
    @SerializedName("references") var references: String = "",
    @SerializedName("remember") var remember: String = "",
    @SerializedName("is_liked") var isLiked: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        title = parcel.readString() ?: "",
        startDate = parcel.readString() ?: "",
        endDate = parcel.readString(),
        language = parcel.readString() ?: "",
        type = parcel.readString() ?: "",
        contents = parcel.readString() ?: "",
        references = parcel.readString() ?: "",
        remember = parcel.readString() ?: "",
        isLiked = parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(language)
        parcel.writeString(type)
        parcel.writeString(contents)
        parcel.writeString(references)
        parcel.writeString(remember)
        parcel.writeByte(if (isLiked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Project> {
        override fun createFromParcel(parcel: Parcel): Project {
            return Project(parcel)
        }

        override fun newArray(size: Int): Array<Project?> {
            return arrayOfNulls(size)
        }
    }
}