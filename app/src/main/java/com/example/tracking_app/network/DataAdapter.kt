package com.example.tracking_app.network

import com.example.tracking_app.database.models.AgentEntity
import com.example.tracking_app.database.models.EventEntity
import com.example.tracking_app.database.models.LocationEntity
import com.example.tracking_app.database.models.RoadMapEntity
import com.example.tracking_app.models.*
import com.example.tracking_app.network.models.*
import com.example.tracking_app.network.models.RoadMapResponse
import com.google.android.gms.location.LocationResult
import okhttp3.internal.toImmutableList
import java.util.*

fun AgentInfoResponse.toAgentEntity(): AgentEntity {
    return AgentEntity(this.Id,this.avatar, this.birthDate, this.city, this.company,
            this.country, this.email, this.firstName, this.jobPosition,
            this.lastName, this.mobile, this.role, this.username)
}

fun AccessTokenResponse.toModelAccessToken(): AccessToken {
    return AccessToken(
        this.accessToken,
        this.expiresIn,
        this.tokenType
    )
}

fun AgentEntity.toAgent(): Agent {
    return Agent(this.AgentId,this.avatar,this.birthDate,this.city,this.company,this.country,this.email,this.firstName,this.lastName,this.jobPosition,this.mobile,this.role,this.username)
}

fun LocationResult.toLocationEntity() : LocationEntity {
    return LocationEntity(0,
        " ",
        this.lastLocation.longitude,
        this.lastLocation.latitude,
        this.lastLocation.altitude,
        this.lastLocation.provider,
        this.lastLocation.accuracy,
        Date(this.lastLocation.time))
}

fun LocationEntity.toLocation() : Location {
    return Location(this.id,this.urn,this.geo_longitude,this.geo_latitude,this.geo_altitude,this.provider,this.accuracy,this.created_at.time)
}

fun List<LocationEntity>.toLocations() : List<Location> {
    val list = arrayListOf<Location>()
    this.forEach {
        list.add(it.toLocation())
    }
    return list
}

fun Location.toLocationEntity() : LocationEntity {
    return LocationEntity(this.id,this.urn,this.geo_longitude,this.geo_latitude,this.geo_altitude,this.provider,this.accuracy,Date(this.created_at))
}

fun List<Location>.toLocationsEntity() : List<LocationEntity> {
    val list = arrayListOf<LocationEntity>()
    this.forEach {
        list.add(it.toLocationEntity())
    }
    return list
}

fun EventRoadMap.toRoadMapEvent() : RoadMapEvent{
    return RoadMapEvent(this.Id,this.IdAgentRoadMap,this.CheckPointId,this.EventDate.time,this.EventType,this.Statut,this.longitude,this.latitude)
}

fun RoadMapResponse.toRoadMapEntity() : RoadMapEntity{
    return RoadMapEntity(this.IdAgentRoadMap,this.ListIdCheckPoint,this.ListCheckPoint,this.StatutCheckPoint)
}

fun List<RoadMapResponse>.toRoadMapEntitys() : List<RoadMapEntity>{
    val list = arrayListOf<RoadMapEntity>()
    this.forEach {
        list.add(it.toRoadMapEntity())
    }
    return list
}

fun RoadMapEntity.toRoadMapResponse() : RoadMapResponse{
    return RoadMapResponse(this.IdAgentRoadMap,this.ListIdCheckPoint,this.ListCheckPoint,this.StatutCheckPoint)
}

fun List<RoadMapEntity>.toRoadMapResponses() : List<RoadMapResponse>{
    val list = mutableListOf<RoadMapResponse>()
    this.forEach {
        list.add(it.toRoadMapResponse())
    }
    return list
}

fun com.example.tracking_app.models.RoadMapResponse.toRoadMapEntity() : RoadMapEntity{
    return RoadMapEntity(this.idAgentRoadMap!!,this.listIdCheckPoint!!,this.listCheckPoints!!,this.statutCheckPoint!!)
}



/*fun EventEntity.toEvent() : Event {
    return Event(this.eventId,this.titre,this.description,this.degre_danger,this.longitude,this.latitude,this.imageURL,this.dejavue)
}*/

/*fun List<EventEntity>.toListEvent() : List<Event> {
    val list = mutableListOf<Event>()
    this.forEach {
        list.add(it.toEvent())
    }
    return list
}*/

/*fun Event.ToEventEntity() : EventEntity {
    return EventEntity(this.id,this.titre,this.description,this.imageURL,Date(this.date),this.longitude,this.latitude,this.degre_danger,this.zone,this.dejavue)
}

fun List<Event>.ToListEventEntity() : List<EventEntity> {
    val list = mutableListOf<EventEntity>()
    this.forEach {
        list.add(it.ToEventEntity())
    }
    return list
}*/

fun EventFBRLresponse.toEvent() : Event{
    return Event(this.id.toString(),this.titre,this.description,this.degre_danger,this.longitude,this.latitude,this.imageURL,this.dejavue,this.date,this.zone,this.categorie)
}

fun Location.toLocationFB() :LocationFB{
    return LocationFB(this.id,this.urn,this.geo_longitude,this.geo_latitude,this.geo_altitude,this.provider,this.accuracy,Date(this.created_at))
}