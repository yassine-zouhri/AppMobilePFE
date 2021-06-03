package com.example.tracking_app.ui.EventDetail

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.tracking_app.R
import com.example.tracking_app.ui.addEvent.AddEventFragmentDirections
import com.example.tracking_app.ui.login.LoginActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.event_details_fragment.*
import kotlinx.android.synthetic.main.fragment_map.*
import java.text.SimpleDateFormat
import java.util.*

class EventDetailsFragment : Fragment() {

    private  var MyPosition : String = ""
    lateinit var handler : Handler
    private lateinit var viewModel: EventDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.event_details_fragment, container, false)

        handler = Handler()
        if(arguments!=null){

            val Myargs :EventDetailsFragmentArgs  = EventDetailsFragmentArgs.fromBundle(requireArguments())
            //val MyPositionEvent :  Int = Myargs.myEventPosition
            //println("Myargs.myEventIDMyargs.myEventIDMyargs.myEventID   "+Myargs.myEventID)
            root.findViewById<TextView>(R.id.validerEventDetail).setOnClickListener{
                root.findViewById<ProgressBar>(R.id.progressBarEventDetail).visibility =View.VISIBLE
                root.findViewById<TextView>(R.id.validerEventDetail).text = "Attend Svp"
                viewModel.ValidateEvent(Myargs.myEventID)
                handler.postDelayed({
                    var action = EventDetailsFragmentDirections.actionEventDetailsFragmentToNavEvents()
                    Navigation.findNavController(root).navigate(action)
                },3000)
            }
        }


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EventDetailsViewModel::class.java)

        viewModel.navigateEvent.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                val navIntent = Intent(context, LoginActivity::class.java)
                navIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(navIntent)
                viewModel.navigationEventDone()}
        })


        if(arguments!=null){
            val Myargs :EventDetailsFragmentArgs  = EventDetailsFragmentArgs.fromBundle(requireArguments())
            val MyPositionEvent :  String = Myargs.myEventID
            MyPosition = MyPositionEvent
            //Toast.makeText(requireContext(),"position "+MyPositionEvent,Toast.LENGTH_SHORT).show()
            MyviewPager.adapter = PageDetailsAdapter(parentFragmentManager,MyPositionEvent)
            tabLayoutEvent.setupWithViewPager(MyviewPager)
        }


        viewModel.GetAllEvents().observe(viewLifecycleOwner, Observer {
            if(it!=null ){
                it.forEach {
                    if(it.eventId.equals(MyPosition)){
                        Eventtilte.text = it.titre
                        EventDescription.text = it.description
                        Eventdate.text = toSimpleString(it.date)
                        EventZone.text = it.zone
                        EventCategorie.text = it.categorie
                        if(it.valider){
                            validerEventDetail.text = "Déjà validé"
                            validerEventDetail.isClickable = false
                        }
                    }
                }
            }
        })



    }

    fun toSimpleString(date: Date?) = with(date ?: Date()) {
        SimpleDateFormat("E, dd MMM yyyy HH:mm").format(this)
    }






}