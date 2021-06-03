package com.example.tracking_app.ui.Events

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tracking_app.database.DataBaseHelperImpl
import com.example.tracking_app.database.DatabaseBuilder
import com.example.tracking_app.database.models.EventEntity
import com.example.tracking_app.models.Agent

import com.example.tracking_app.network.ApiHelperImpl
import com.example.tracking_app.network.getNetworkService
import com.example.tracking_app.network.getNetworkServiceWithBearer
import com.example.tracking_app.repository.AuthenticationRepo
import com.example.tracking_app.repository.EventRepository
import com.example.tracking_app.utils.SessionManagerUtil
import com.example.tracking_app.utils.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _navigateEvent = MutableLiveData(false)
    val navigateEvent: LiveData<Boolean>
        get() = _navigateEvent

    private val eventRepo =
        EventRepository(
            ApiHelperImpl(getNetworkServiceWithBearer()),
            DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance())
        )
    private val authenticationRepo =
        AuthenticationRepo(
            ApiHelperImpl(getNetworkService()),
            DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance())
        )


    private val _CurrentUser = MutableLiveData<Agent>()
    val CurrentUser: LiveData<Agent>
        get() = _CurrentUser


    init {
        if (SessionManagerUtil.isSessionActive() == SessionStatus.AccessTokenExpired) {
            navigationEventStart()
        }else{
            UpdateNewEventStatus()
            viewModelScope.launch {
                _CurrentUser.value = authenticationRepo.GetCurrentUser()
            }
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

    fun UpdateNewEventStatus(){
        viewModelScope.launch {
            eventRepo.UpdateNewEventStatus()
        }
    }


}