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
package com.example.tracking_app.database.converter

import androidx.room.TypeConverter
import com.example.tracking_app.database.AppDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Date
import java.util.UUID

/**
 * Converts non-standard objects in the {@link MyLocation} data class into and out of the database.
 */
class LocationTypeConverters {

    val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @ExperimentalStdlibApi
    @TypeConverter
    fun fromlistIdCheckPoint(listIdCheckPoint : List<Long>?): String?{
        return moshi.adapter<List<Long>>().toJson(listIdCheckPoint)
    }

    @ExperimentalStdlibApi
    @TypeConverter
    fun tolistIdCheckPoint(value : String?) : List<Long>?{
        return  moshi.adapter<List<Long>>().fromJson(value)
    }

    @ExperimentalStdlibApi
    @TypeConverter
    fun fromListCheckPoint(listCheckPoint : List<List<Double>>?): String?{
        return moshi.adapter<List<List<Double>>>().toJson(listCheckPoint)
    }

    @ExperimentalStdlibApi
    @TypeConverter
    fun toListCheckPoint(value : String?) : List<List<Double>>?{
        return moshi.adapter<List<List<Double>>>().fromJson(value)
    }

    @ExperimentalStdlibApi
    @TypeConverter
    fun fromStatutCheckPoint(statutCheckPoint : List<Boolean>?): String?{
        return moshi.adapter<List<Boolean>>().toJson(statutCheckPoint)
    }

    @ExperimentalStdlibApi
    @TypeConverter
    fun toStatutCheckPoint(value : String?) : List<Boolean>?{
        return moshi.adapter<List<Boolean>>().fromJson(value)
    }
}
