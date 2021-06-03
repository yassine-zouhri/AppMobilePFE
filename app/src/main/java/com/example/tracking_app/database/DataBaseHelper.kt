package com.example.tracking_app.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.tracking_app.database.models.AgentEntity
import com.example.tracking_app.database.models.EventEntity
import com.example.tracking_app.database.models.LocationEntity
import com.example.tracking_app.database.models.RoadMapEntity
import com.example.tracking_app.models.Event
import com.example.tracking_app.network.models.TacheItems

interface DataBaseHelper {

    suspend fun insertAgent(agent: AgentEntity)

    suspend fun getAgent(): AgentEntity

    suspend fun clearAgent()

    suspend fun insertLocation(location : LocationEntity)

    suspend fun getLocations() : List<LocationEntity>

    suspend fun deleteLocation(id : Long )

    suspend fun deleteLocations(locations : List<LocationEntity>)

    suspend fun insertRoadMaps(roadMaps : List<RoadMapEntity>)

    suspend fun insertRoadMap(roadMap : RoadMapEntity)

    fun getRoadMaps() : LiveData<List<RoadMapEntity>>

    suspend fun getRoadMapsbyIdAgentRoadMap(id : Long) :  RoadMapEntity

    suspend fun deleteRoadMap(id : Long)

    suspend fun UpdateRoadMaps(IdAgentRoadMap : Long , CheckPointId : Long)

    suspend fun DeleteRoadMapOnfinish(IdAgentRoadMap: Long)

    fun getdatatest() :List<RoadMapEntity>

    suspend fun deleteAllroadMap()

    fun getAllEvents() : LiveData<List<EventEntity>>

    suspend fun AddEvent(event : Event)

    suspend fun AddNewEvent(event : EventEntity)

    suspend fun AddEvents(events : List<Event>)

    suspend fun UpdateNewEventStatus(status : Boolean)

    suspend fun CountNewEvents () : Int

    suspend fun UpdateValidatedEventStatus(status : Boolean,id : String)

    fun  GetAllEventnotValidated() : LiveData<List<TacheItems>>

    companion object {
        @Volatile private var INSTANCE: DataBaseHelper? = null
        fun getInstance(context: Context): DataBaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance()).also { INSTANCE = it }
            }
        }
    }


}