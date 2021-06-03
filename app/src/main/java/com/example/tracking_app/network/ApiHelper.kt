package com.example.tracking_app.network


import com.example.tracking_app.database.models.LocationEntity
import com.example.tracking_app.models.DeclareEvent
import com.example.tracking_app.models.FCMuserToken
import com.example.tracking_app.network.models.*

interface ApiHelper {

    suspend fun auth(
        userName: String,
        password: String
    ): AccessTokenResponse


    suspend fun GetRoadMaps(AgentId : Long) : List<RoadMapResponse>

    suspend fun SendEventRoadMap( event : RoadMapEvent)

    suspend fun SendPositionsToAPI (positions: List<Location>)  : List<Location>?

    suspend fun SendTokenApp(fCMuserToken : FCMuserToken)

    suspend fun deleteFCMToken(AgentId: Long)

    suspend fun AddEvent(event: DeclareEvent)

    suspend fun UpdateEvent(idEvent : Long)
}