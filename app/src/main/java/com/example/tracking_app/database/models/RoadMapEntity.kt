package com.example.tracking_app.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "roadMap_table")
class RoadMapEntity (

        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "idAgentRoadMap") val IdAgentRoadMap: Long = 0,
        @ColumnInfo(name = "listIdCheckPoint") val ListIdCheckPoint: List<Long>,
        @ColumnInfo(name = "listCheckPoint") val ListCheckPoint: List<List<Double>>,
        @ColumnInfo(name = "statutCheckPoint") var StatutCheckPoint: List<Boolean>

)