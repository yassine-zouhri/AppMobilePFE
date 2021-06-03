package com.example.tracking_app.models

data class RoadMapResponse (
    val idAgentRoadMap: Long? = null,
    val listIdCheckPoint : List<Long>? = null,
    val listCheckPoints: List<List<Double>>? = null,
    val statutCheckPoint: List<Boolean>? = null,
    val username : String? = null
)