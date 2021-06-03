package com.example.tracking_app.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tracking_app.R
import com.example.tracking_app.network.models.EventRoadMap
import com.example.tracking_app.network.models.RoadMapResponse
import com.example.tracking_app.network.toRoadMapResponses
import com.example.tracking_app.servise.GeofenceService.GeofenceHelper
import com.example.tracking_app.servise.GeofenceService.createChannel
import com.example.tracking_app.ui.login.LoginActivity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.*


class MapFragment : Fragment() , OnMapReadyCallback{

    private lateinit var mapViewModel: MapViewModel
    val listPoints = arrayListOf<LatLng>()
    lateinit var CurrentGeofence :LatLng
    var OptionsRoadMap  = mutableListOf<String>()
    //lateinit var listRoadMap : List<RoadMapResponse>
    var listRoadMap  = mutableListOf<RoadMapResponse>()
    //Geofencing
    private lateinit var geofencingClient: GeofencingClient
    private var geofenceHelper: GeofenceHelper? = null
    private val GEOFENCE_ID = "SOME_GEOFENCE_ID"
    private val FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
    private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002



    private lateinit var map: GoogleMap
    private val TAG = MapFragment::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1

    private val POLYLINE_STROKE_WIDTH_PX = 25


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapViewModel.GetMyRoadMaps().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if(it.size>0){
                listRoadMap = it.toRoadMapResponses().toMutableList()
                OnchangeRoadMap(listRoadMap,0)

                var indexRoadMap = 1
                OptionsRoadMap.clear()
                it.forEach {
                    OptionsRoadMap.add("RoadMap "+ indexRoadMap)
                    indexRoadMap++
                }
            }
            if(it.size ==0){
                listPoints.clear()
                listRoadMap.toMutableList().clear()
                map.clear()
            }
            onMapReady(map)
        })
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.drop_down_roadmap_item,OptionsRoadMap)
        RoadMapSelected.setAdapter(arrayAdapter)

        RoadMapSelected?.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                OnchangeRoadMap(listRoadMap,position)
            }
        }
        mapGeofence.onCreate(savedInstanceState)
        mapGeofence.onResume()
        mapGeofence.getMapAsync(this)

    }


    fun OnchangeRoadMap(listRoadMap : List<RoadMapResponse>, indexRoadMap : Int){
        listPoints.clear()
        map.clear()
        listRoadMap.get(indexRoadMap).ListCheckPoint.forEach {
            listPoints.add(LatLng(it.get(1),it.get(0)))
        }
        AddMarker(listPoints,indexRoadMap)
        var index :Int = 0
        for(a in listRoadMap.get(indexRoadMap).StatutCheckPoint ){
            if(!a){
                CurrentGeofence = listPoints.get(index)
                EventRoadMap.CheckPointId = listRoadMap.get(indexRoadMap).ListIdCheckPoint.get(index)
                AddGeofenceOnCheckPoint(CurrentGeofence,70f)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(CurrentGeofence, 17f))
                break
            }
            index++
        }

        EventRoadMap.IdAgentRoadMap = listRoadMap.get(indexRoadMap).IdAgentRoadMap
        onMapReady(map)
    }



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mapViewModel = ViewModelProvider(this,MapViewModelFactory(requireContext())).get(MapViewModel::class.java)

        mapViewModel.navigateEvent.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                val navIntent = Intent(context, LoginActivity::class.java)
                navIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(navIntent)
                mapViewModel.navigationEventDone()}
        })


        val root = inflater.inflate(R.layout.fragment_map, container, false)

        createChannel(requireContext())
        geofencingClient = LocationServices.getGeofencingClient(requireContext())
        geofenceHelper = GeofenceHelper(requireContext())

        return root
    }

    fun AddMarker(listPoints :List<LatLng>,indexRoadMap : Int){
        var index = 0
        listRoadMap.get(indexRoadMap).StatutCheckPoint.forEach {
            if(it){
                map.addMarker(MarkerOptions().position(listPoints.toMutableList().get(index))).setIcon(BitmapFromVector(requireContext(), R.drawable.ic_check))
            }else{
                map.addMarker(MarkerOptions().position(listPoints.toMutableList().get(index)))
            }
            index++
        }
    }

    private fun BitmapFromVector(context: Context, vectorResId: Int ):BitmapDescriptor  {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)

        if (vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight())
        }

        val bitmap: Bitmap = Bitmap.createBitmap(vectorDrawable!!.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        vectorDrawable?.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }



    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        val polyline = googleMap.addPolyline(
            PolylineOptions()
            .clickable(true))
        polyline.points = listPoints
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline.tag = "A"
        stylePolyline(polyline)

        enableMyLocation()
    }



    private fun AddGeofenceOnCheckPoint(latLng : LatLng , radius: Float ) {
        map.addCircle(
                CircleOptions()
                        .center(latLng)
                        .radius(radius.toDouble())
                        //.strokeColor(-0xDF5B10)
                        //.fillColor(-0x98E993)
                        //.strokeColor(4)
                        .strokeColor(Color.argb(255,255,0,0))
                        .fillColor(Color.argb(64,255,0,0))
                        .strokeColor(4)
        )
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                addGeofence(latLng, radius)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(requireActivity(), permissionsArray, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), permissionsArray, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            addGeofence(latLng, radius)
        }
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


    // Callback for the result from requesting permissions.
    // This method is invoked for every call on requestPermissions(android.app.Activity, String[],
    // int).
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(requireContext(), "You can add geofences...", Toast.LENGTH_SHORT).show()
            } else {
                //We do not have the permission..
                Toast.makeText(requireContext(), "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show()
            }
        }
    }



    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */


    private fun stylePolyline(polyline: Polyline) {
        val pattern = listOf(
            Dot(), Gap(20F), Dash(30F), Gap(20F)
        )
        polyline.pattern = pattern
        polyline.endCap = RoundCap()
        polyline.width = POLYLINE_STROKE_WIDTH_PX.toFloat()
        polyline.color = -0xDF5B10
        polyline.jointType = JointType.ROUND
    }

    private fun addGeofence(latLng: LatLng, radius: Float) {
        val geofence: Geofence? = geofenceHelper?.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER or  Geofence.GEOFENCE_TRANSITION_EXIT)
        val geofencingRequest: GeofencingRequest? = geofenceHelper?.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent? = geofenceHelper?.getPendingIntentNew()
        geofencingClient.removeGeofences(pendingIntent)?.run {
            addOnSuccessListener {
                Toast.makeText(context,"geofences_removed", Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
            }
        }
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener { Log.d(TAG, "onSuccess: Geofence Added...") }
            .addOnFailureListener { e ->
                val errorMessage: String? = geofenceHelper?.getErrorString(e)
                Log.d(TAG, "onFailure: $errorMessage")
            }


    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
    }
}


