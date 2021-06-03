package com.example.tracking_app.models

data class AccessToken(

    val accessToken: String,

    val expiresIn: Long,

    val tokenType: String
)