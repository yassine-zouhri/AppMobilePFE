package com.example.tracking_app.utils

import com.example.tracking_app.models.AccessToken
import com.example.tracking_app.utils.GeoApplication.Companion.getSessionPreferences
import java.util.*

object SessionManagerUtil {

    private val sessionPreferences = getSessionPreferences()

    private const val ACCESS_TOKEN = "access_token"
    private const val ACCESS_TOKEN_EXP = "access_token_exp"

    fun CreateSessionPreferencesFile() {
        //todo: add security layer to this section
        with(sessionPreferences.edit()) {
            commit()
        }
    }

    fun startUserSession(accessToken: AccessToken) {
        //todo: add security layer to this section
        with(sessionPreferences.edit()) {

            putString(ACCESS_TOKEN, accessToken.accessToken)
            putLong(ACCESS_TOKEN_EXP, accessToken.expiresIn.toLong())
            commit()
        }
    }

    fun isSessionActive(): SessionStatus {
        //todo: add security layer to this section

        val currentTimeStamp = (Math.floor((Date().time / 1000).toDouble()) * 1000).toLong()
        val accessTokenExp = sessionPreferences.getLong(ACCESS_TOKEN_EXP, 0)



        if (accessTokenExp == 0L)
            return SessionStatus.AccessTokenExpired

        return if (Date(currentTimeStamp).before(Date(accessTokenExp)))
            SessionStatus.Valid
        else
            SessionStatus.AccessTokenExpired
    }


    fun endUserSession() {
        //todo: add security layer to this section
        sessionPreferences.edit().clear().apply()
    }

    fun getSessionAccessToken(): String? {
        return sessionPreferences.getString(ACCESS_TOKEN, "")
    }
}

enum class SessionStatus {
    Valid,
    AccessTokenExpired
}