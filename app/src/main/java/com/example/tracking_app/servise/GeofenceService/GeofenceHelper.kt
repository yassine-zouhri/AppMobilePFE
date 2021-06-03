package com.example.tracking_app.servise.GeofenceService

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tracking_app.database.DataBaseHelperImpl
import com.example.tracking_app.database.DatabaseBuilder
import com.example.tracking_app.network.ApiHelperImpl
import com.example.tracking_app.network.getNetworkServiceWithBearer
import com.example.tracking_app.network.models.EventRoadMap
import com.example.tracking_app.network.models.RoadMapResponse
import com.example.tracking_app.network.toRoadMapResponses
import com.example.tracking_app.repository.RoadMapRepository
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng


class GeofenceHelper(base: Context) : ContextWrapper(base) {

    private val TAG = "GeofenceHelper"
    var pendingIntent: PendingIntent? = null
    val RoadMapRepo =
            RoadMapRepository(
                    ApiHelperImpl(getNetworkServiceWithBearer()),
                    DataBaseHelperImpl(DatabaseBuilder.getDatabaseInstance())
            )
    private lateinit var geofencingClient: GeofencingClient
    private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002

    fun getGeofencingRequest(geofence: Geofence?): GeofencingRequest? {
        return GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build()
    }

    fun getGeofence(ID: String?, latLng: LatLng, radius: Float, transitionTypes: Int): Geofence? {
        return Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000*60)
                .setNotificationResponsiveness(5000*60)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
    }

    fun getPendingIntentNew(): PendingIntent? {
        if (pendingIntent != null) {
            return pendingIntent
        }
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return pendingIntent
    }

    fun getErrorString(e: Exception): String? {
        if (e is ApiException) {
            when (e.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return "GEOFENCE_TOO_MANY_GEOFENCES"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return "GEOFENCE_TOO_MANY_PENDING_INTENTS"
            }
        }
        return e.localizedMessage
    }

    fun AddGeofenceOnCheckPoint(latLng : LatLng , radius: Float ) {
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                addGeofence(latLng, radius)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(Activity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(Activity(), permissionsArray, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(Activity(), permissionsArray, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            addGeofence(latLng, radius)
        }
    }

    fun addGeofence(latLng: LatLng, radius: Float) {
        geofencingClient = LocationServices.getGeofencingClient(applicationContext)
        val geofence: Geofence? =getGeofence("SOME_GEOFENCE_ID", latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER or  Geofence.GEOFENCE_TRANSITION_EXIT)
        val geofencingRequest: GeofencingRequest? = getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent? = getPendingIntentNew()
        geofencingClient.removeGeofences(pendingIntent)?.run {
            addOnSuccessListener {
                Toast.makeText(applicationContext,"geofences_removed", Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
            }
        }
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener { Log.d(TAG, "onSuccess: Geofence Added...") }
                .addOnFailureListener { e ->
                    val errorMessage: String? = getErrorString(e)
                    Log.d(TAG, "onFailure: $errorMessage")
                }
    }

    fun OnchangeRoadMap(){
        val indexRoadMap = 0
        var listRoadMap : List<RoadMapResponse> = RoadMapRepo.getdatatest().toRoadMapResponses()
        if(listRoadMap.size>0){

            val listPoints = arrayListOf<LatLng>()
            lateinit var CurrentGeofence :LatLng
            listRoadMap.get(indexRoadMap).ListCheckPoint.forEach {
                listPoints.add(LatLng(it.get(1),it.get(0)))
            }
            var index :Int = 0
            for(a in listRoadMap.get(indexRoadMap).StatutCheckPoint ){
                if(!a){
                    CurrentGeofence = listPoints.get(index)
                    println(" EventRoadMap.CheckPointId EventRoadMap.CheckPointId EventRoadMap.CheckPointId ="+ EventRoadMap.CheckPointId)
                    EventRoadMap.CheckPointId = listRoadMap.get(indexRoadMap).ListIdCheckPoint.get(index)
                    AddGeofenceOnCheckPoint(CurrentGeofence,70f)
                    break
                }
                index++
            }

            EventRoadMap.IdAgentRoadMap = listRoadMap.get(indexRoadMap).IdAgentRoadMap

        }
    }





}