package com.example.tracking_app.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(tableName = "agents_table")

data class AgentEntity(

    @ColumnInfo(name = "AgentId") var AgentId: Long,
    @ColumnInfo(name = "avatar") var avatar: String,
    @ColumnInfo(name = "birthDate")val birthDate: String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "company") val company: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "firstName") val firstName: String,
    @ColumnInfo(name = "jobPosition") val jobPosition: String,
    @ColumnInfo(name = "lastName") val lastName: String,
    @ColumnInfo(name = "mobile") val mobile: String,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "username") val username: String,

    //@ColumnInfo(name = "registration_number") val registrationNumber: String,
    //@ColumnInfo(name = "cin") val cin: String,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Long = 0



)