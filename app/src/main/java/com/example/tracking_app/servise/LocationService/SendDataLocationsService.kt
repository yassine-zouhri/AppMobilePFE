package com.example.tracking_app.servise.LocationService

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.tracking_app.database.DataBaseHelperImpl
import com.example.tracking_app.database.DatabaseBuilder
import com.example.tracking_app.database.models.LocationEntity
import com.example.tracking_app.network.*
import com.example.tracking_app.network.models.Location
import com.example.tracking_app.repository.LocationRepository
import com.example.tracking_app.utils.SessionManagerUtil
import com.example.tracking_app.utils.SessionStatus
import kotlinx.coroutines.*

class SendDataLocationsService : Service() {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private val locationRepo =
            LocationRepository(
                    ApiHelperImpl(getNetworkServiceWithBearer()),
                    DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance())
            )

    val SendDataResponse = locationRepo.SendDataPositionsResponse

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        viewModelScope.launch(Dispatchers.IO) {
            while(SessionManagerUtil.isSessionActive() == SessionStatus.Valid) {
                var locations : List<LocationEntity> = locationRepo.getLocations()
                val MyPositions : List<Location>? = locationRepo.SendPositionsToAPI(locations.toLocations())
                if(MyPositions != null){
                    locationRepo.DeleteLocations(MyPositions.toLocationsEntity())
                    locationRepo.UpdateDataLocation(MyPositions);
                }
                delay(5000)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }



}