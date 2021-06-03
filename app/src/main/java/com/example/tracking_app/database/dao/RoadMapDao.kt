package com.example.tracking_app.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tracking_app.database.models.RoadMapEntity

@Dao
interface RoadMapDao {

    @Query("SELECT * FROM roadMap_table ")
    fun getRoadMaps1() : List<RoadMapEntity>

    @Query("SELECT * FROM roadMap_table ")
    fun getRoadMaps() : LiveData<List<RoadMapEntity>>

    @Query("SELECT * FROM roadMap_table WHERE idAgentRoadMap=(:id)")
    fun  getRoapMapByIdAgentRoadMap(id : Long) : RoadMapEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRoadMap(roadMap : RoadMapEntity)

    @Query("DELETE FROM roadMap_table WHERE idAgentRoadMap=(:id) ")
    suspend fun deleteRoadMap(id : Long)

    @Query("DELETE FROM roadMap_table")
    suspend fun deleteAllRoadMap()


}