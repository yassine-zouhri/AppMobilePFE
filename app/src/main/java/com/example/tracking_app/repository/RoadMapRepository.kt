package com.example.tracking_app.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.tracking_app.database.DataBaseHelper
import com.example.tracking_app.database.models.RoadMapEntity
import com.example.tracking_app.models.EventFBRLresponse
import com.example.tracking_app.network.*
import com.example.tracking_app.network.models.EventRoadMap
import com.example.tracking_app.network.models.RoadMapEvent
import com.example.tracking_app.network.models.RoadMapResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RoadMapRepository(private val apiHelper: ApiHelper,
                        private val dataBaseHelper: DataBaseHelper
) {

    /*suspend fun InsertRoadMap(){
        val roadmaps : List<RoadMapResponse> = apiHelper.GetRoadMaps(EventRoadMap.AgentId)
        roadmaps.forEach {
            dataBaseHelper.insertRoadMaps(roadmaps.toRoadMapEntitys())
        }
    }*/

    fun GetRoadMaps(): LiveData<List<RoadMapEntity>>{
        return dataBaseHelper.getRoadMaps()
    }

    suspend fun UpdateRoadMapStatut(IdAgentRoadMap : Long , CheckPointId : Long){
        dataBaseHelper.UpdateRoadMaps(IdAgentRoadMap,CheckPointId)
    }



    suspend fun SendEventRoadMap(event : RoadMapEvent) = apiHelper.SendEventRoadMap(event)

    suspend fun DeleteRoadMapOnfinish(IdAgentRoadMap : Long) =  dataBaseHelper.DeleteRoadMapOnfinish(IdAgentRoadMap)

    suspend fun DeleteAll() = dataBaseHelper.deleteAllroadMap()

    fun getdatatest() : List<RoadMapEntity> = dataBaseHelper.getdatatest()

    suspend fun UpdateRoadMap(){
        var token : String =""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            token = task.result
        })
        var databaseRef= FirebaseDatabase.getInstance("https://geo-app1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("roadsMap")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for(ds in dataSnapshot.children){
                    val event = ds.getValue(com.example.tracking_app.models.RoadMapResponse::class.java)


                    GlobalScope.launch (Dispatchers.IO) {
                        if((event!!.username).equals(dataBaseHelper.getAgent()?.toAgent().username)){
                            dataBaseHelper.insertRoadMap(event!!.toRoadMapEntity())
                            databaseRef.child(ds.key!!).removeValue()
                        }
                    }
                    println(event)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    companion object {
        @Volatile private var INSTANCE: RoadMapRepository? = null

        fun getInstance(context: Context): RoadMapRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RoadMapRepository(
                    ApiHelperImpl(getNetworkServiceWithBearer()),
                    DataBaseHelper.getInstance(context))
                    .also { INSTANCE = it }
            }
        }
    }

}