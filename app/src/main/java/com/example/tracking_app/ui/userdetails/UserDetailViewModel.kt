package com.example.tracking_app.ui.userdetails

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tracking_app.database.DataBaseHelperImpl
import com.example.tracking_app.database.DatabaseBuilder
import com.example.tracking_app.models.Agent
import com.example.tracking_app.network.ApiHelperImpl
import com.example.tracking_app.network.getNetworkService
import com.example.tracking_app.network.getNetworkServiceWithBearer
import com.example.tracking_app.repository.AuthenticationRepo
import com.example.tracking_app.repository.ManageAgentRepo
import com.example.tracking_app.repository.RoadMapRepository
import com.example.tracking_app.utils.SessionManagerUtil
import com.example.tracking_app.utils.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UserDetailViewModel(private val context: Context) : ViewModel() {


    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    private val manageAgentRepo = ManageAgentRepo(DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance()))
    private val authenticationRepo =
        AuthenticationRepo(
            ApiHelperImpl(getNetworkService()),
            DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance())
        )

    val RoadMapRepo =
        RoadMapRepository(
            ApiHelperImpl(getNetworkServiceWithBearer()),
            DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance())
        )

    private val _CurrentUser = MutableLiveData<Agent>()
    val CurrentUser: LiveData<Agent>
        get() = _CurrentUser




    private val _navigateEvent = MutableLiveData(false)
    val navigateEvent: LiveData<Boolean>
        get() = _navigateEvent

    init {
        viewModelScope.launch {
            _CurrentUser.value = authenticationRepo.GetCurrentUser()
        }

        if (SessionManagerUtil.isSessionActive() == SessionStatus.AccessTokenExpired)
            navigationEventStart()
    }
    fun navigationEventDone() {
        _navigateEvent.value = false
    }

    fun navigationEventStart() {
        _navigateEvent.value = true
    }

    fun logout() {
        viewModelScope.launch {
            //RoadMapRepo.DeleteAll()
            val IdAgent : Long = authenticationRepo.GetCurrentUser().IdAgent
            authenticationRepo.DeleteTokenFCM(IdAgent)
            manageAgentRepo.clearAllData()
            SessionManagerUtil.endUserSession()
        }
    }

    val logoutState = manageAgentRepo.clearAllDataStatus

}