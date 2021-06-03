package com.example.tracking_app.models

import java.io.Serializable

data class Agent (

        val IdAgent : Long ,
        var avatar: String,
        val birthDate: String,
        val city: String,
        val company: String,
        val country: String,
        val email: String,
        val firstName: String,
        val lastName: String,
        val jobPosition: String,
        val mobile: String,
        val role: String,
        val username: String
        //val registrationNumber: String,
        //val cin: String

):Serializable