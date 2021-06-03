package com.example.tracking_app.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgentInfoResponse (

    @Json(name = "id")
    val Id: Long,

    @Json(name = "username")
    val username: String,

    @Json(name = "role")
    val role: String,

    @Json(name = "firstName")
    val firstName: String,

    @Json(name = "lastName")
    val lastName: String,

    @Json(name = "email")
    val email: String,

    @Json(name = "birthDate")
    val birthDate: String,

    @Json(name = "city")
    val city: String,

    @Json(name = "country")
    val country: String,

    @Json(name = "avatar")
    val avatar: String,

    @Json(name = "company")
    val company: String,

    @Json(name = "jobPosition")
    val jobPosition: String,

    @Json(name = "mobile")
    val mobile: String
)