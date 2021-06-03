package com.example.tracking_app.network

import com.example.tracking_app.database.models.LocationEntity
import com.example.tracking_app.models.DeclareEvent
import com.example.tracking_app.models.FCMuserToken
import com.example.tracking_app.network.models.*
import retrofit2.http.*

interface ApiService {

    @POST("/api/auth2")
    suspend fun getAccessToken(@Body authCredential: AuthCredentials): AccessTokenResponse

    @POST("/api/getTokenApp")
    suspend fun SentTokenApp(@Body fCMuserToken : FCMuserToken)

    @POST("/api/deleteToken")
    suspend fun deleteFCMToken(@Body agentId: Long)

    @GET("/agent/me/roadMap/{agentId}")
    suspend fun GetRoadMaps(@Path("agentId") agentId : Long) : List<RoadMapResponse>

    @POST("/agent/me/EventRoadMap")
    suspend fun SendEventRoadMap(@Body event : RoadMapEvent)

    @POST("/agent/me/positions")
    suspend fun SendPositionsToAPI(@Body listLocations: List<Location>) : List<Location>

    @POST("/agent/AddEvent")
    suspend fun  SendEventToAPI(@Body event: DeclareEvent)

    @POST("/agent/updateStatutEvent/{statut}/{idEvent}")
    suspend fun  UpdateStatutEvent(@Path("statut") statut : Boolean,@Path("idEvent") idEvent : Long)

}