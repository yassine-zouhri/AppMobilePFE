package com.example.tracking_app.network.models

import com.squareup.moshi.Json

data class RoadMapEvent (

    @Json(name = "id")
    val id: Long = 0,

    @Json(name = "idAgentRoadMap")
    val IdAgentRoadMap: Long = 0,

    @Json(name = "checkPointId")
    val CheckPointId: Long = 0,

    @Json(name = "eventDate")
    val EventDate: Long = 0,

    @Json(name = "eventType")
    val EventType: String = "Enter",

    @Json(name = "statut")
    val Statut: Boolean = false,

    @Json(name = "longitude")
    val longitude : Double = 0.0,

    @Json(name = "latitude")
    val latitude: Double = 0.0

)