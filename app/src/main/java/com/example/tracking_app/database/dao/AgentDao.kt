package com.example.tracking_app.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tracking_app.database.models.AgentEntity

@Dao
interface AgentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agentEntity: AgentEntity)

    @Query("SELECT * FROM agents_table LIMIT 1")
    suspend fun getAgent(): AgentEntity

    @Query("DELETE FROM agents_table")
    suspend fun clearAgent()
}