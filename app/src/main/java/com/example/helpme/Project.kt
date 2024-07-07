package com.example.helpme

import android.os.Parcel
import android.os.Parcelable

data class Project(
    val title: String,
    val startDate: String,
    val endDate: String?,
    val language: String,
    val type: String,
    var contents: String,
    var isLiked: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",  // Add this line to read contents
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(language)
        parcel.writeString(type)
        parcel.writeString(contents)  // Add this line to write contents
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
