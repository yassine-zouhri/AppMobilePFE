package com.example.tracking_app.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tracking_app.database.models.EventEntity
import com.example.tracking_app.network.models.TacheItems

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun AddEvent(event : EventEntity)

    @Query("SELECT * FROM events_table ORDER BY date desc ")
    fun GetEvents() : LiveData<List<EventEntity>>

    @Query("SELECT * FROM events_table ")
    fun GetEvents1() : List<EventEntity>

    @Query("SELECT * FROM events_table WHERE eventId=(:id)")
    suspend fun GetEventById(id : Long) : EventEntity

    @Query("UPDATE events_table SET dejavue = (:status) ")
    suspend fun UpdateNewEventStatus(status : Boolean)

    @Query("UPDATE events_table SET valider = (:status) WHERE eventId=(:id) ")
    suspend fun UpdateValidatedEventStatus(status : Boolean , id : String)

    @Query("SELECT eventId,titre FROM events_table WHERE valider=(:bool) ")
    fun GetAllEventNotValidated(bool : Boolean) : LiveData<List<TacheItems>>
}