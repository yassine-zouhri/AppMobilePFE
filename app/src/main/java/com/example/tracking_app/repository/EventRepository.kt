package com.example.tracking_app.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.tracking_app.database.DataBaseHelper
import com.example.tracking_app.database.models.EventEntity
import com.example.tracking_app.models.DeclareEvent
import com.example.tracking_app.models.Event
import com.example.tracking_app.models.EventFBRLresponse
import com.example.tracking_app.network.ApiHelper
import com.example.tracking_app.network.ApiHelperImpl
import com.example.tracking_app.network.getNetworkServiceWithBearer
import com.example.tracking_app.network.models.TacheItems
import com.example.tracking_app.network.toEvent
import com.example.tracking_app.utils.toByteArray
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class EventRepository(private val apiHelper: ApiHelper, private val dataBaseHelper: DataBaseHelper) {


    fun GetEvents() : LiveData<List<EventEntity>> = dataBaseHelper.getAllEvents()


    fun UpdateDataEvent(){
        var token : String =""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            token = task.result
        })
        var databaseRef= FirebaseDatabase.getInstance("https://geo-app1-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("events")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for(ds in dataSnapshot.children){
                    val event = ds.getValue(EventFBRLresponse::class.java)
                    if(event !=null && token.equals(event.token)){
                        println("rrrr        +"+event)
                        GlobalScope.launch (Dispatchers.IO) {
                            AddNewEvent(event.toEvent())
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    suspend fun UpdateNewEventStatus(){
        dataBaseHelper.UpdateNewEventStatus(true)
    }

    suspend fun DeclareEvent(event: DeclareEvent){
        apiHelper.AddEvent(event)
    }

    suspend fun AddNewEvent(event : Event){
        try {
            val BitmapImgae : Bitmap? = getBitmapFromURL(event.imageURL)
            //al newUUID = UUID.randomUUID().toString()
            val MyEvent : EventEntity = EventEntity(event.id,event.titre,event.description,BitmapImgae!!.toByteArray(),
                Date(event.date),event.longitude,event.latitude,event.degre_danger,event.zone,event.dejavue,event.categorie,false)
                dataBaseHelper.AddNewEvent(MyEvent)
        }catch (exp : Exception){
            println("erreur : "+exp.message)
        }
    }

    suspend fun ValidateEvent(status :Boolean,id : String){
        dataBaseHelper.UpdateValidatedEventStatus(status,id)
        apiHelper.UpdateEvent(id.toLong())
    }



    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url
                .openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun GetAllEventNotValidated() :  LiveData<List<TacheItems>> = dataBaseHelper.GetAllEventnotValidated()


    companion object {
        @Volatile private var INSTANCE:  EventRepository? = null

        fun getInstance(context: Context):  EventRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?:  EventRepository(
                    ApiHelperImpl(getNetworkServiceWithBearer()),
                    DataBaseHelper.getInstance(context))
                    .also { INSTANCE = it }
            }
        }
    }




}