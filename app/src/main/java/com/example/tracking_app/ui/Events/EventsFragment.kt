package com.example.tracking_app.ui.Events

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tracking_app.R
import com.example.tracking_app.database.models.EventEntity
//import com.example.tracking_app.network.toListEvent
import com.example.tracking_app.ui.login.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text
import java.util.ArrayList


class EventsFragment : Fragment(),EventAdapter.OnItemClickListener {

    private lateinit var eventsViewModel: EventsViewModel
    private lateinit var MyEventsOrdered : List<EventEntity>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventsViewModel = ViewModelProvider(this).get(EventsViewModel::class.java)

        eventsViewModel.navigateEvent.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                eventsViewModel.navigationEventDone()
                val navIntent = Intent(context, LoginActivity::class.java)
                navIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(navIntent)
                }
        })

        val root = inflater.inflate(R.layout.fragment_events, container, false)
        // recuperer le recycler view vertical
        var verticalRecyclerView = root.findViewById<RecyclerView>(R.id.EventRecyclerView)


        eventsViewModel.GetAllEvents().observe(viewLifecycleOwner, Observer {
            MyEventsOrdered = OrderEvents(it)
            verticalRecyclerView.adapter = EventAdapter(MyEventsOrdered,this)
            val MyListner = this
            root.findViewById<EditText>(R.id.search_barEvent).addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    MyEventsOrdered = OrderEvents(EventAdapter(it,MyListner).FilterMyEvents(s,it))
                    verticalRecyclerView.adapter = EventAdapter(MyEventsOrdered,MyListner)
                }
            })
        })


        /*root.findViewById<FloatingActionButton>(R.id.ButtonAddEvent).setOnClickListener {
            var action = EventsFragmentDirections.actionNavEventsToAddEventFragment()
            Navigation.findNavController(root).navigate(action)
        }*/


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        eventsViewModel.UpdateNewEventStatus()
    }

    override fun onItemClick(position: Int) {
        var action = EventsFragmentDirections.actionNavEventsToEventDetailsFragment()
        action.setMyEventPosition(position)
        action.setMyEventID(MyEventsOrdered.get(position).eventId)
        this.findNavController().navigate(action)
    }

    fun OrderEvents(events : List<EventEntity>) : List<EventEntity> {
        val LitsEventsNoValidated: MutableList<EventEntity> = ArrayList<EventEntity>()
        val LitsEventsValidated : MutableList<EventEntity> = ArrayList<EventEntity>()
        val LitsAllEvents: MutableList<EventEntity> = ArrayList<EventEntity>()
        events.forEach {
            if(it.valider){LitsEventsValidated.add(it)}
            else {LitsEventsNoValidated.add(it)}
        }
        LitsEventsNoValidated.sortByDescending { it.date }
        LitsEventsValidated.sortByDescending { it.date }
        LitsEventsNoValidated.forEach {
            LitsAllEvents.add(it)
        }
        LitsEventsValidated.forEach {
            LitsAllEvents.add(it)
        }
        return LitsAllEvents
    }


}