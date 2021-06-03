package com.example.tracking_app.database

import androidx.lifecycle.LiveData
import com.example.tracking_app.database.models.AgentEntity
import com.example.tracking_app.database.models.EventEntity
import com.example.tracking_app.database.models.LocationEntity
import com.example.tracking_app.database.models.RoadMapEntity
import com.example.tracking_app.models.Event
//import com.example.tracking_app.network.ToEventEntity
import com.example.tracking_app.network.models.EventRoadMap
import com.example.tracking_app.network.models.TacheItems
import com.example.tracking_app.utils.SessionManagerUtil
import com.example.tracking_app.utils.SessionStatus
import com.example.tracking_app.utils.getRandomString

class DataBaseHelperImpl(private val appDatabase: AppDatabase) : DataBaseHelper {
    override suspend fun insertAgent(agent: AgentEntity) {
        appDatabase.agentDao().insertAgent(agent)
    }

    override suspend fun getAgent(): AgentEntity {
        return appDatabase.agentDao().getAgent()
    }

    override suspend fun clearAgent() {
        appDatabase.agentDao().clearAgent()
    }

    override suspend fun insertLocation(location: LocationEntity) {
        var urn : String
        if (SessionManagerUtil.isSessionActive() == SessionStatus.Valid){
            urn = location.geo_latitude.toString()+":"+
                    location.geo_longitude.toString()+":"+
                    location.geo_altitude.toString()+":"+
                    appDatabase.agentDao().getAgent().AgentId.toString()+":"+
                    getRandomString(30)
            location.urn = urn
            appDatabase.locationDao().addLocation(location)
        }
    }

    override suspend fun getLocations(): List<LocationEntity> {
        return appDatabase.locationDao().getLocations()
    }

    override suspend fun deleteLocation(id: Long) {
        appDatabase.locationDao().DeteleLocation(id)
    }

    override suspend fun deleteLocations(locations: List<LocationEntity>) {
        locations.forEach {
            appDatabase.locationDao().DeteleLocation(it.id)
        }
    }

    override suspend fun insertRoadMaps(roadMaps: List<RoadMapEntity>) {
        roadMaps.forEach {
            appDatabase.roadMapDao().addRoadMap(it)
        }
    }

    override suspend fun insertRoadMap(roadMap: RoadMapEntity) {
        appDatabase.roadMapDao().addRoadMap(roadMap)
    }

    override fun getRoadMaps(): LiveData<List<RoadMapEntity>> {
        return appDatabase.roadMapDao().getRoadMaps()
    }

    override suspend fun getRoadMapsbyIdAgentRoadMap(id: Long): RoadMapEntity {
        val roadMap : RoadMapEntity = appDatabase.roadMapDao().getRoapMapByIdAgentRoadMap(id)
        return roadMap
    }

    override suspend fun deleteRoadMap(id: Long) {
        appDatabase.roadMapDao().deleteRoadMap(id)
    }

    override suspend fun UpdateRoadMaps(IdAgentRoadMap: Long, CheckPointId: Long) {
        var roadMap :RoadMapEntity = appDatabase.roadMapDao().getRoapMapByIdAgentRoadMap(IdAgentRoadMap)
        if(roadMap!=null){
            var index = 0
            for(a in roadMap.ListIdCheckPoint){
                if(a==CheckPointId){break}
                index++
            }
            var e : MutableList<Boolean> = roadMap.StatutCheckPoint.toMutableList()
            e[index] = true
            roadMap.StatutCheckPoint = e
            insertRoadMap(roadMap)
        }
    }

    override suspend fun DeleteRoadMapOnfinish(IdAgentRoadMap: Long) {
        val roadMap : RoadMapEntity =  appDatabase.roadMapDao().getRoapMapByIdAgentRoadMap(IdAgentRoadMap)

        if(roadMap != null){

            val listStatus : List<Boolean> = roadMap.StatutCheckPoint
            var condition = 0
            listStatus.forEach {
                if(it){condition++}
            }
            if(condition==listStatus.size){
                appDatabase.roadMapDao().deleteRoadMap(IdAgentRoadMap)
            }
        }
    }

    override fun getdatatest():List<RoadMapEntity>  {
        return appDatabase.roadMapDao().getRoadMaps1()
    }

    override suspend fun deleteAllroadMap() {
        appDatabase.roadMapDao().deleteAllRoadMap()
    }

    override fun getAllEvents(): LiveData<List<EventEntity>> {
        return appDatabase.eventDao().GetEvents()
    }

    override suspend fun AddEvent(event: Event) {
        //appDatabase.eventDao().AddEvent(event.ToEventEntity())
    }

    override suspend fun AddNewEvent(event: EventEntity) {
        appDatabase.eventDao().AddEvent(event)
    }

    override suspend fun AddEvents(events: List<Event>) {
        events.forEach {
            AddEvent(it)
        }
    }

    override suspend fun UpdateNewEventStatus(status: Boolean) {
        appDatabase.eventDao().UpdateNewEventStatus(status)
    }

    override suspend fun CountNewEvents(): Int {
        var nombre = 0
        println("it.dejavue)it.dejavue)it.dejavue)it.dejavue) "+appDatabase.eventDao().GetEvents1().size)
        appDatabase.eventDao().GetEvents1().forEach {
            println("it.dejavue)it.dejavue)it.dejavue)it.dejavue) "+it.dejavue)
            if(!it.dejavue){
                nombre ++
            }
        }
        /*appDatabase.eventDao().GetEvents().value?.forEach {

            if(!it.dejavue){
                nombre ++
            }
        }*/
        println("nombre = 0nombre = 0nombre = 0nombre = 0nombre = 0 = "+nombre)
        return nombre
    }

    override suspend fun UpdateValidatedEventStatus(status: Boolean,id : String) {
        appDatabase.eventDao().UpdateValidatedEventStatus(status,id)
    }

    override fun GetAllEventnotValidated(): LiveData<List<TacheItems>> {
        return  appDatabase.eventDao().GetAllEventNotValidated(false)
    }


}