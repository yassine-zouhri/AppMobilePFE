package com.example.tracking_app.network.models

import com.squareup.moshi.Json

data class AuthCredentials(

    @Json(name = "username")
    val userName: String,

    @Json(name = "password")
    val password: String
)