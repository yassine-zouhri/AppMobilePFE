package com.example.tracking_app.ui.splashScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tracking_app.database.DataBaseHelperImpl
import com.example.tracking_app.database.DatabaseBuilder
import com.example.tracking_app.network.ApiHelperImpl
import com.example.tracking_app.network.getNetworkServiceWithBearer
import com.example.tracking_app.repository.AuthenticationRepo
import com.example.tracking_app.repository.LocationRepository
import com.example.tracking_app.repository.ManageAgentRepo
import com.example.tracking_app.utils.SessionManagerUtil
import com.example.tracking_app.utils.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SplashScreenActivityViewModel : ViewModel() {

    var navigateEvent : Boolean = false

    init {
        if (SessionManagerUtil.isSessionActive() == SessionStatus.AccessTokenExpired) {
            navigateEvent = true
        }
    }
}