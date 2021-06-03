package com.example.tracking_app.network.models

import com.squareup.moshi.Json

data  class TacheItems (
    @Json(name = "eventId")
    var eventId : String,

    @Json(name = "titre")
    var titre : String
)
