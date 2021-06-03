package com.example.tracking_app.ui.EventDetailImage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tracking_app.R
import com.example.tracking_app.ui.EventDetail.EventDetailsViewModel
import com.example.tracking_app.ui.login.LoginActivity
import com.example.tracking_app.utils.toBitmap
import kotlinx.android.synthetic.main.fragment_event_detail_image.*


class EventDetailImageFragment(MyPositionEvent : String) : Fragment() {

    val MyEventsId : String = MyPositionEvent

    private lateinit var viewModel: EventDetailImageViewModel

            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_detail_image, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EventDetailImageViewModel::class.java)
        viewModel.navigateEvent.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                val navIntent = Intent(context, LoginActivity::class.java)
                navIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(navIntent)
                viewModel.navigationEventDone()}
        })
        //println("MyPositionMyPositionMyPositionMyPosition     ="+MyPosition)
        viewModel.GetAllEvents().observe(viewLifecycleOwner, Observer {
            if(it!=null){
                it.forEach {
                    if(it.eventId.equals(MyEventsId)){
                        ImageEventDetail.setImageBitmap(it.imageURL.toBitmap())
                    }
                }
            }
        })

    }

}