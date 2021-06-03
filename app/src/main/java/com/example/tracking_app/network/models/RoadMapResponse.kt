package com.example.tracking_app.network.models

import com.squareup.moshi.Json

data class RoadMapResponse (

    @Json(name = "idAgentRoadMap")
    val IdAgentRoadMap: Long = 0,

    @Json(name = "listIdCheckPoint")
    var ListIdCheckPoint : List<Long>,

    @Json(name = "listCheckPoint")
    var ListCheckPoint : List<List<Double>>,

    @Json(name = "statutCheckPoint")
    var StatutCheckPoint : List<Boolean>
)