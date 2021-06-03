package com.example.tracking_app.network.models

import com.example.tracking_app.database.models.AgentEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgentResponse(


    @Json(name = "sub")
    val username: String,

    @Json(name = "type")
    val tokenType: String,

    @Json(name = "exp")
    val expiresIn: String,

    @Json(name = "user")
    val user: AgentInfoResponse



        /* @Json(name = "registrationNumber")
         val registrationNumber: String,

         @Json(name = "cin")
         val cin: String,*/


)