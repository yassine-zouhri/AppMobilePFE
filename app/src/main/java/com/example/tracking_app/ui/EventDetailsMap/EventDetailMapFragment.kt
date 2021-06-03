package com.example.tracking_app.ui.EventDetailsMap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tracking_app.R
import com.example.tracking_app.ui.EventDetail.EventDetailsViewModel
import com.example.tracking_app.ui.login.LoginActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.event_details_fragment.*
import kotlinx.android.synthetic.main.fragment_event_detail_map.*


class EventDetailMapFragment(MyPositionEvent : String) : Fragment(), OnMapReadyCallback {

    private var MyEventId : String = MyPositionEvent
    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 2
    private lateinit var viewModel: EventDetailMapViewModel
    var CurrentGeofence : LatLng? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_detail_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =  ViewModelProvider(this).get(EventDetailMapViewModel::class.java)

        viewModel.GetAllEvents().observe(viewLifecycleOwner, Observer {
            if(it!=null){
                it.forEach {
                    if(it.eventId.equals(MyEventId)){
                        CurrentGeofence = LatLng(it.latitude,it.longitude)
                        AddMarkerToMap(CurrentGeofence!!)
                    }
                }
            }
        })

        viewModel.navigateEvent.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                val navIntent = Intent(context, LoginActivity::class.java)
                navIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(navIntent)
                viewModel.navigationEventDone()}
        })

        mapEventDetail.onCreate(savedInstanceState)
        mapEventDetail.onResume()
        mapEventDetail.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
    }

    fun AddMarkerToMap(point : LatLng){
        map.clear()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 17f))
        map.apply {
            addMarker(
                MarkerOptions()
                    .position(point)
                    .title("Evenement")
            )
        }
        onMapReady(map)
    }

    // Checks that users have given permission
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Checks if users have given their location and sets location enabled if so.
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        }
        else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onPause() {
        super.onPause()
        map.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        map.clear()
    }

    override fun onStop() {
        super.onStop()
        map.clear()
    }

}