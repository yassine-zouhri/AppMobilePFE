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
package com.example.tracking_app.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tracking_app.database.models.LocationEntity
import java.util.UUID

/**
 * Defines database operations.
 */
@Dao
interface LocationDao {

    @Query("SELECT * FROM location_table ")
    suspend fun getLocations(): List<LocationEntity>

    @Query("SELECT * FROM location_table WHERE id=(:id)")
    suspend fun getLocation(id: Long): LocationEntity

    @Update
    suspend fun updateLocation(myLocationEntity: LocationEntity)

    @Insert
    suspend fun addLocation(myLocationEntity: LocationEntity)

    @Insert
    suspend fun addLocations(myLocationEntities: List<LocationEntity>)

    @Query("DELETE FROM location_table WHERE id=(:id) ")
    suspend fun DeteleLocation(id: Long)

}
