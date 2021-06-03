package com.example.tracking_app.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.tracking_app.R

class GeoApplication: Application() {



    override fun onCreate() {
        super.onCreate()

        application = this

        userPref = this.getSharedPreferences(getString(R.string.user_preference), Context.MODE_PRIVATE)

        sessionPref = this.getSharedPreferences(getString(R.string.session_preference), Context.MODE_PRIVATE)
    }







    companion object {

        private lateinit var userPref: SharedPreferences
        private lateinit var sessionPref: SharedPreferences
        private lateinit var application: Application

        fun getUserPreferences() = userPref

        fun getSessionPreferences() = sessionPref

        fun getApplicationContext() = application


        private var activityVisible = false
        fun isActivityVisible(): Boolean {
            return activityVisible
        }

        fun activityResumed() {
            activityVisible = true
        }

        fun activityPaused() {
            activityVisible = false
        }
    }
}