package com.example.tracking_app.servise.GeofenceService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.example.tracking_app.database.DataBaseHelperImpl
import com.example.tracking_app.database.DatabaseBuilder
import com.example.tracking_app.network.ApiHelperImpl
import com.example.tracking_app.network.getNetworkServiceWithBearer
import com.example.tracking_app.network.models.EventRoadMap
import com.example.tracking_app.network.toLocationEntity
import com.example.tracking_app.network.toRoadMapEvent
import com.example.tracking_app.repository.LocationRepository
import com.example.tracking_app.repository.RoadMapRepository
import com.example.tracking_app.ui.main.MainActivity
import com.example.tracking_app.ui.map.MapFragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class GeofenceBroadcastReceiver : BroadcastReceiver() {


    private val TAG = "GeofenceBroadcastReceiv"

    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationHelper = NotificationHelper(context!!)

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        val geofenceHelper = GeofenceHelper(context)

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...")
            return
        }

        val geofenceList = geofencingEvent.triggeringGeofences
        for (geofence in geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.requestId)
        }
        val location : Location = geofencingEvent.getTriggeringLocation();

        val transitionType = geofencingEvent.geofenceTransition

        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MapFragment::class.java)
                EventRoadMap.EventType = "Enter"
                EventRoadMap.EventDate = Date()
                EventRoadMap.Statut = false
                EventRoadMap.longitude = location.longitude
                EventRoadMap.latitude = location.latitude
                GlobalScope.launch (Dispatchers.IO) {
                    RoadMapRepository.getInstance(context).SendEventRoadMap(EventRoadMap.toRoadMapEvent())
                }

            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapFragment::class.java)
                EventRoadMap.EventType="Exit"
                EventRoadMap.EventDate = Date()
                EventRoadMap.Statut = true
                EventRoadMap.longitude = location.longitude
                EventRoadMap.latitude = location.latitude
                GlobalScope.launch (Dispatchers.IO) {
                    RoadMapRepository.getInstance(context).SendEventRoadMap(EventRoadMap.toRoadMapEvent())
                    RoadMapRepository.getInstance(context).UpdateRoadMapStatut(EventRoadMap.IdAgentRoadMap,EventRoadMap.CheckPointId)
                    RoadMapRepository.getInstance(context).DeleteRoadMapOnfinish(EventRoadMap.IdAgentRoadMap)
                    geofenceHelper.OnchangeRoadMap()
                }
            }
        }

    }


}



