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
package com.example.tracking_app.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tracking_app.database.DataBaseHelper
import com.example.tracking_app.database.models.LocationEntity
import com.example.tracking_app.models.EventFBRLresponse
import com.example.tracking_app.models.NetworkResponseState
import com.example.tracking_app.network.*
import com.example.tracking_app.network.models.Location
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class LocationRepository (
    private val apiHelper: ApiHelper,
    private val dataBaseHelper: DataBaseHelper
) {

    private val _SendDataPositionsResponse = MutableLiveData<NetworkResponseState<List<Location>>>()
    val SendDataPositionsResponse: LiveData<NetworkResponseState<List<Location>>>
        get() = _SendDataPositionsResponse


    suspend fun getLocations(): List<LocationEntity> = dataBaseHelper.getLocations()

    suspend fun addLocation(myLocationEntity: LocationEntity) {
        dataBaseHelper.insertLocation(myLocationEntity)
    }

    suspend fun DeleteLocations(locations : List<LocationEntity>){
        dataBaseHelper.deleteLocations(locations)
    }

    suspend fun SendPositionsToAPI(positions: List<Location>) : List<Location>? {
        try{
            val MyPositions : List<Location>?  = apiHelper.SendPositionsToAPI(positions)
            return MyPositions
        }catch (Exp : Exception){}
        return null
    }

    suspend fun UpdateDataLocation(locations : List<Location>){
        var token : String =""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            token = task.result
        })
        var databaseRef= FirebaseDatabase.getInstance("https://geo-app1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("AgentLocation1")
        var databaseRef2= FirebaseDatabase.getInstance("https://geo-app1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("agentsCurrentLocation")
        var CurrentAgentUsername :String? = null;
        if(dataBaseHelper.getAgent()!=null){
            CurrentAgentUsername = dataBaseHelper.getAgent().AgentId.toString()
            /*locations.forEach {
                databaseRef.child(CurrentAgentUsername).child(UUID.randomUUID().toString()).setValue(it.toLocationFB())


            }*/



            val location= hashMapOf<String, Any?>()
            if(locations.size>0){
                databaseRef.child(CurrentAgentUsername).child(locations.get(locations.lastIndex).created_at.toString()).setValue(locations.get(locations.lastIndex))
                location["lat"] = locations.get(locations.lastIndex).geo_latitude;location["lng"] = locations.get(locations.lastIndex).geo_longitude
                databaseRef2.child(CurrentAgentUsername).removeValue()
                databaseRef2.child(CurrentAgentUsername).setValue(location)
            }
        }
    }





    companion object {
        @Volatile private var INSTANCE: LocationRepository? = null

        fun getInstance(context: Context): LocationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocationRepository(
                    ApiHelperImpl(getNetworkServiceWithBearer()),
                    DataBaseHelper.getInstance(context))
                    .also { INSTANCE = it }
            }
        }
    }
}

