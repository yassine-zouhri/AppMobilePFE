package com.example.tracking_app.ui.main

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.tracking_app.BuildConfig
import com.example.tracking_app.R
import com.example.tracking_app.servise.LocationService.ForegroundOnlyLocationService
import com.example.tracking_app.servise.LocationService.SendDataLocationsService
import com.example.tracking_app.ui.login.LoginActivity
import com.example.tracking_app.utils.GeoApplication
import com.example.tracking_app.utils.toText
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() , SharedPreferences.OnSharedPreferenceChangeListener {


    /**********  Localisation Service  ********/
    lateinit var addEvent : FloatingActionButton
    private  val TAG = "MainActivity"
    private  val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
    private var foregroundOnlyLocationServiceBound = false

    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null

    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver

    private lateinit var sharedPreferences: SharedPreferences


    private var duration :Long = 4

    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationService!!.locationRequest.interval= TimeUnit.SECONDS.toMillis(duration)
            foregroundOnlyLocationService!!.locationRequest.fastestInterval= TimeUnit.SECONDS.toMillis(duration)
            foregroundOnlyLocationService!!.locationRequest.maxWaitTime= TimeUnit.SECONDS.toMillis(duration)
            foregroundOnlyLocationServiceBound = true




            /********  Démarrer le service de localisation  **************/
            Thread.sleep(1000) // wait for 2 seconds
            if (foregroundPermissionApproved()) {
                foregroundOnlyLocationService?.subscribeToLocationUpdates()
                        ?: Log.d(TAG, "Service Not Bound")
            } else {
                requestForegroundPermissions()
            }

            /***********************/

        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }



    /**********************************************************/


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var viewModel : MainActivityModel


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var intentSendData = Intent(this,SendDataLocationsService::class.java)
        startService(intentSendData)

        //var intentSendData1 = Intent(this,MyFirebaseMessagingService::class.java)
        //startService(intentSendData1)
        //lifecycle.addObserver(GeofenceBroadcastReceiver)
        /***    Login Session     ***/
        viewModel = MainActivityModel()
        viewModel.logoutState.observe(this, Observer{
            val myService = Intent(this, ForegroundOnlyLocationService::class.java)
            stopService(myService)
            val navIntent = Intent(this, LoginActivity::class.java)
            navIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            viewModel.navigationEventDone()
            FirebaseMessaging.getInstance().deleteToken()
            startActivity(navIntent)
        })
        viewModel.navigateEvent.observe(this, Observer {
            if(it){
                val myService = Intent(this, ForegroundOnlyLocationService::class.java)
                stopService(myService)
                val sendPositionService = Intent(this, SendDataLocationsService::class.java)
                stopService(sendPositionService)
                val navIntent = Intent(this, LoginActivity::class.java)
                navIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                viewModel.navigationEventDone()
                startActivity(navIntent)
            }
        })

        /***********************/

        /*********** Nav configuration  ********/
        /*val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)*/

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navBottomView : BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navBottomView.background = null
        navBottomView.menu.getItem(2).isEnabled = false
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.nav_home,R.id.nav_events,R.id.nav_map
                ), drawerLayout
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navBottomView.setupWithNavController(navController)

        val badge :  BadgeDrawable  =  navBottomView.getOrCreateBadge(R.id.nav_events)

        viewModel.GetAllEvents().observe(this, Observer {
            var nombre = 0

            it.forEach{
                if(!it.dejavue){ nombre++ }
            }
            if(nombre == 0){
                badge.clearNumber()
                badge.setVisible(false)
            }else{
                badge.backgroundColor = Color.RED
                badge.badgeTextColor = Color.WHITE
                badge.maxCharacterCount = 3
                badge.setVisible(true)
                badge.number = nombre
            }
        })


        //FirebaseMessaging.getInstance().subscribeToTopic("NEWYORK_WEATHER");

        badge.setVisible(true)

        navBottomView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_events -> {
                    badge.setVisible(false)
                    navController.navigate(R.id.nav_events)
                }
                R.id.nav_home -> {

                    navController.navigate(R.id.nav_home)
                }
                R.id.nav_map -> {
                    navController.navigate(R.id.nav_map)
                }
                R.id.TODOFragment -> {
                    navController.navigate(R.id.TODOFragment)
                }
            }
            true
        }


        /*******************************/

        /*********** Service configuration************/
        var TimeUpdateLocation: Long = intent.getLongExtra("duration",4)
        duration = TimeUpdateLocation
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

        sharedPreferences =
                getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        /********************************/
        addEvent= findViewById(R.id.addEvent)
        addEvent.setOnClickListener {
            navController.navigate(R.id.addEventFragment)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println("lateinit lateinit lateinit lateinit +  ="+item.itemId)
        when (item.itemId) {
            R.id.action_logout -> {
                foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
                viewModel.logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val serviceIntent = Intent(this, ForegroundOnlyLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
                foregroundOnlyBroadcastReceiver,
                IntentFilter(
                        ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
        GeoApplication.activityResumed()
    }

    override fun onPause() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                foregroundOnlyBroadcastReceiver
        )
        GeoApplication.activityPaused()
        super.onPause()
    }

    override fun onStop() {

        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        super.onStop()
    }

    override fun onDestroy() {
        foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
        super.onDestroy()
    }


    // TODO: Step 1.0, Review Permissions: Method checks if permissions approved.
    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    // TODO: Step 1.0, Review Permissions: Method requests permissions.
    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            Snackbar.make(
                    findViewById(R.id.drawer_layout),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_LONG
            )
                    .setAction(R.string.ok) {
                        // Request permission
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                        )
                    }
                    .show()
        } else {
            Log.d(TAG, "Request foreground only permission")
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    // TODO: Step 1.0, Review Permissions: Handles permission result.
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult")

        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive empty arrays.
                    Log.d(TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    // Permission was granted.
                    foregroundOnlyLocationService?.subscribeToLocationUpdates()
                else -> {
                    // Permission denied.

                    Snackbar.make(
                            findViewById(R.id.drawer_layout),
                            R.string.permission_denied_explanation,
                            Snackbar.LENGTH_LONG
                    )
                            .setAction(R.string.settings) {
                                // Build intent that displays the App settings screen.
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts(
                                        "package",
                                        BuildConfig.APPLICATION_ID,
                                        null
                                )
                                intent.data = uri
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                            .show()
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }

    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                    ForegroundOnlyLocationService.EXTRA_LOCATION
            )

            if (location != null) {
                /*logResultsToScreen("Foreground location: ${location.toText()}")*/
                println("ForegroundOnlyBroadcastReceiver   " +location.toText())
            }
        }
    }



}

