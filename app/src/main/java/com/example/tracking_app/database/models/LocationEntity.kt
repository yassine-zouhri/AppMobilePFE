/*
 * Copyright (C) 2020 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.tracking_app.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
import java.util.Date
import java.util.UUID

/**
 * Data class for Location related data (only takes what's needed from
 * {@link android.location.Location} class).
 */
@Entity(tableName = "location_table")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "urn") var urn : String = " ",
    @ColumnInfo(name = "geo_longitude") val geo_longitude: Double = 0.0,
    @ColumnInfo(name = "geo_latitude") val geo_latitude: Double = 0.0,
    @ColumnInfo(name = "geo_altitude") val geo_altitude: Double = 0.0,
    @ColumnInfo(name = "provider") val provider: String = "Device",
    @ColumnInfo(name = "accuracy") val accuracy: Float = 0.0F,
    @ColumnInfo(name = "created_at") val created_at: Date = Date()
)