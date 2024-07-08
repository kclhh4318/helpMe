package com.example.helpme.model

data class Project(
    var proj_id: Int,
    var title: String,
    var start_d: String,
    var end_d: String?,
    var lang: String?,
    var type: String,
    var email: String
)
