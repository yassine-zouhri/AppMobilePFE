package com.example.tracking_app.ui.map

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.tracking_app.database.DataBaseHelperImpl
import com.example.tracking_app.database.DatabaseBuilder
import com.example.tracking_app.database.models.RoadMapEntity
import com.example.tracking_app.models.NetworkResponseState
import com.example.tracking_app.network.ApiHelperImpl
import com.example.tracking_app.network.getNetworkServiceWithBearer
import com.example.tracking_app.network.models.EventRoadMap
import com.example.tracking_app.network.models.Location
import com.example.tracking_app.network.models.RoadMapResponse
import com.example.tracking_app.network.toLocationsEntity
import com.example.tracking_app.repository.LocationRepository
import com.example.tracking_app.repository.ManageAgentRepo
import com.example.tracking_app.repository.RoadMapRepository
import com.example.tracking_app.utils.SessionManagerUtil
import com.example.tracking_app.utils.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MapViewModel(private val context: Context) : ViewModel() {


    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.IO)




    val RoadMapRepo =
        RoadMapRepository(
            ApiHelperImpl(getNetworkServiceWithBearer()),
            DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance())
        )

    private val _navigateEvent = MutableLiveData(false)
    val navigateEvent: LiveData<Boolean>
        get() = _navigateEvent

    init {
        if (SessionManagerUtil.isSessionActive() == SessionStatus.AccessTokenExpired) {
            navigationEventStart()
            viewModelScope.launch {
                RoadMapRepo.DeleteAll()
            }
        }else{
            viewModelScope.launch {
                //RoadMapRepo.InsertRoadMap()
            }
        }
    }

    fun GetMyRoadMaps() :  LiveData<List<RoadMapEntity>>{
        return RoadMapRepo.GetRoadMaps()
    }

    fun navigationEventDone() {
        _navigateEvent.value = false
    }

    fun navigationEventStart() {
        _navigateEvent.value = true
    }


}