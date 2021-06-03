package com.example.tracking_app.database

import android.content.Context
import androidx.room.Room
import com.example.tracking_app.utils.GeoApplication

object DatabaseBuilder {

    private var INSTANCE: AppDatabase? = null

    fun getDatabaseInstance(): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                INSTANCE = buildRoomDB(GeoApplication.getApplicationContext())
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =

        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "GeoTrackingBDD"
        ).build()

}