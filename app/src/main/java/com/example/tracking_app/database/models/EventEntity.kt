package com.example.tracking_app.database.models

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "events_table")
data class EventEntity (

    @PrimaryKey @ColumnInfo(name = "eventId") var eventId : String,
    @ColumnInfo(name = "titre") var titre : String,
    @ColumnInfo(name = "description") var description : String,
    @ColumnInfo(name = "imageURL",typeAffinity = ColumnInfo.BLOB) var imageURL : ByteArray,
    @ColumnInfo(name = "date") var date : Date,
    @ColumnInfo(name = "longitude") var longitude : Double,
    @ColumnInfo(name = "latitude") var latitude : Double,
    @ColumnInfo(name = "degre_danger") var degre_danger : Int,
    @ColumnInfo(name = "zone") var zone : String,
    @ColumnInfo(name = "dejavue") var dejavue : Boolean,
    @ColumnInfo(name = "categorie") var categorie : String,
    @ColumnInfo(name = "valider") var valider : Boolean

)
