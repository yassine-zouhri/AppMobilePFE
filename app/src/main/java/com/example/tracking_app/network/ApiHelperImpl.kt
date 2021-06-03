package com.example.tracking_app.network

import com.example.tracking_app.database.models.LocationEntity
import com.example.tracking_app.models.DeclareEvent
import com.example.tracking_app.models.FCMuserToken
import com.example.tracking_app.network.models.*
import java.io.IOException
import java.lang.Exception

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {

    override suspend fun auth(
        userName: String,
        password: String
    ) = apiService.getAccessToken(AuthCredentials(userName, password))

    override suspend fun GetRoadMaps(AgentId : Long): List<RoadMapResponse> = apiService.GetRoadMaps(AgentId)

    override suspend fun SendEventRoadMap(event: RoadMapEvent) = apiService.SendEventRoadMap(event)

    override suspend fun SendPositionsToAPI(positions: List<Location>): List<Location>? {
        try {
            val locations :List<Location> = apiService.SendPositionsToAPI(positions)
            Thread.sleep(1000)
            if(locations!=null){
                return  locations
            }
        }catch (Exp : Exception){
            println("Exp = "+Exp.message)
        }
       return null
    }

    override suspend fun SendTokenApp(fCMuserToken: FCMuserToken) {
        apiService.SentTokenApp(fCMuserToken)
    }

    override suspend fun deleteFCMToken(AgentId: Long) {
        apiService.deleteFCMToken(AgentId)
    }

    override suspend fun AddEvent(event: DeclareEvent) {
        try {
            apiService.SendEventToAPI(event)
        }catch (exp : IOException){}
    }

    override suspend fun UpdateEvent(idEvent : Long) {
        apiService.UpdateStatutEvent(true,idEvent)
    }


}