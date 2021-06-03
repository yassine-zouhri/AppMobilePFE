package com.example.tracking_app.network.models

import com.squareup.moshi.Json

data class AccessTokenResponse(

    @Json(name = "accessToken")
    val accessToken: String,

    @Json(name = "expiresIn")
    val expiresIn: Long,

    @Json(name = "type")
    val tokenType: String
)