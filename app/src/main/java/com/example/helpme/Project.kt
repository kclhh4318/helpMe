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
    var isLiked: Boolean = false,
    var remember: String = "",
    var reference: String = "",
    var likes: Int = 0,
    val email: String // 이메일 필드 추가
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "" // 이메일 읽기
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(language)
        parcel.writeString(type)
        parcel.writeString(contents)
        parcel.writeByte(if (isLiked) 1 else 0)
        parcel.writeString(remember)
        parcel.writeString(reference)
        parcel.writeInt(likes)
        parcel.writeString(email) // 이메일 쓰기
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
