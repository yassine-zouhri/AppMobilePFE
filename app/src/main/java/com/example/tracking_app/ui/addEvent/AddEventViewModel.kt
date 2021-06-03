package com.example.tracking_app.ui.addEvent

import androidx.lifecycle.ViewModel
import com.example.tracking_app.database.DataBaseHelperImpl
import com.example.tracking_app.database.DatabaseBuilder
import com.example.tracking_app.models.DeclareEvent
import com.example.tracking_app.network.ApiHelperImpl
import com.example.tracking_app.network.getNetworkServiceWithBearer
import com.example.tracking_app.repository.EventRepository
import com.example.tracking_app.repository.RoadMapRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddEventViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    val eventRepo =
        EventRepository(
            ApiHelperImpl(getNetworkServiceWithBearer()),
            DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance())
        )
    fun DeclareSomeEvent(event: DeclareEvent){
        viewModelScope.launch {
            eventRepo.DeclareEvent(event)
        }
    }
}