package com.example.tracking_app.ui.main

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.tracking_app.database.DataBaseHelperImpl
import com.example.tracking_app.database.DatabaseBuilder
import com.example.tracking_app.database.models.EventEntity
import com.example.tracking_app.database.models.LocationEntity
import com.example.tracking_app.models.Agent
import com.example.tracking_app.models.NetworkResponseState
import com.example.tracking_app.network.*
import com.example.tracking_app.network.models.EventRoadMap
import com.example.tracking_app.network.models.Location
import com.example.tracking_app.repository.*
import com.example.tracking_app.utils.SessionManagerUtil
import com.example.tracking_app.utils.SessionStatus
import kotlinx.coroutines.*

class MainActivityModel : ViewModel() {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val manageAgentRepo = ManageAgentRepo(DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance()))

    val RoadMapRepo =
            RoadMapRepository(
                    ApiHelperImpl(getNetworkServiceWithBearer()),
                    DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance())
            )
    private val authenticationRepo =
        AuthenticationRepo(
            ApiHelperImpl(getNetworkService()),
            DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance())
        )

    private val eventRepo =
        EventRepository(
            ApiHelperImpl(getNetworkServiceWithBearer()),
            DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance())
        )



    val logoutState = manageAgentRepo.clearAllDataStatus


    private val _navigateEvent = MutableLiveData(false)
    val navigateEvent: LiveData<Boolean>
        get() = _navigateEvent

    fun logout() {
        viewModelScope.launch {
            RoadMapRepo.DeleteAll()
            val IdAgent : Long = authenticationRepo.GetCurrentUser().IdAgent
            authenticationRepo.DeleteTokenFCM(IdAgent)
            manageAgentRepo.clearAllData()
            SessionManagerUtil.endUserSession()
        }
    }

    init {

        if (SessionManagerUtil.isSessionActive() == SessionStatus.AccessTokenExpired) {
            navigationEventStart()
            viewModelScope.launch {
                RoadMapRepo.DeleteAll()
            }

        }
        else{
            viewModelScope.launch {
                EventRoadMap.AgentId = authenticationRepo.GetCurrentUser().IdAgent
                RoadMapRepo.UpdateRoadMap()
            }
            eventRepo.UpdateDataEvent()
        }
    }

    fun navigationEventDone() {
        _navigateEvent.value = false
    }

    fun navigationEventStart() {
        _navigateEvent.value = true
    }


    fun GetAllEvents() : LiveData<List<EventEntity>>{
        return  eventRepo.GetEvents()
    }



}