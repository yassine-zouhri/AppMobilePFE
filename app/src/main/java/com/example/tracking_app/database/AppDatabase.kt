package com.example.tracking_app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tracking_app.database.converter.LocationTypeConverters
import com.example.tracking_app.database.dao.AgentDao
import com.example.tracking_app.database.dao.EventDao
import com.example.tracking_app.database.dao.LocationDao
import com.example.tracking_app.database.dao.RoadMapDao
import com.example.tracking_app.database.models.AgentEntity
import com.example.tracking_app.database.models.EventEntity
import com.example.tracking_app.database.models.LocationEntity
import com.example.tracking_app.database.models.RoadMapEntity
import com.squareup.moshi.Moshi

@Database(
    entities = arrayOf(AgentEntity::class,LocationEntity::class,RoadMapEntity::class,EventEntity::class) ,
    version = 1,
    exportSchema = false
)
@TypeConverters(LocationTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun agentDao(): AgentDao

    abstract fun locationDao(): LocationDao

    abstract fun roadMapDao() : RoadMapDao

    abstract fun eventDao() : EventDao
}