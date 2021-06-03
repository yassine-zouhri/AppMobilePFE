package com.example.tracking_app.models

import com.squareup.moshi.Json
import java.util.*

class LocationFB (
        @Json(name = "id")
        val id: Long = 0,

        @Json(name = "urn")
        var urn : String = " ",

        @Json(name = "geo_longitude")
        val geo_longitude: Double = 0.0,

        @Json(name = "geo_latitude")
        val geo_latitude: Double = 0.0,

        @Json(name = "geo_altitude")
        val geo_altitude: Double = 0.0,

        @Json(name = "provider")
        val provider: String = "Device",

        @Json(name = "accuracy")
        val accuracy: Float = 0.0F,

        @Json(name = "created_at")
        val created_at: Date
)