package com.example.tracking_app.ui.todoList

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tracking_app.R
import com.example.tracking_app.database.models.EventEntity
import com.example.tracking_app.network.models.EventRoadMap
import com.example.tracking_app.network.models.Location
import com.example.tracking_app.network.models.TacheItems
import com.example.tracking_app.network.toRoadMapEvent
import com.example.tracking_app.repository.RoadMapRepository
import com.example.tracking_app.ui.Events.EventsViewModel
import com.example.tracking_app.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TODOFragment : Fragment(),TacheAdapter.OnItemClickListener {


    private lateinit var viewModel: TODOViewModel
    private lateinit var MyEventsNotValidated : List<TacheItems>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this).get(TODOViewModel::class.java)
        var root = inflater.inflate(R.layout.t_o_d_o_fragment, container, false)
        var verticalRecyclerView = root.findViewById<RecyclerView>(R.id.TacheRecyclerView)

        viewModel.navigateEvent.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                viewModel.navigationEventDone()
                val navIntent = Intent(context, LoginActivity::class.java)
                navIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(navIntent)
            }
        })
        GlobalScope.launch (Dispatchers.IO) {
            root.findViewById<TextView>(R.id.fullNameAgentTaches).text = viewModel.GetCurrentUser().lastName+" "+viewModel.GetCurrentUser().firstName
        }
        viewModel.GetAllEventsNotValidated().observe(viewLifecycleOwner, Observer {
            MyEventsNotValidated = it
            verticalRecyclerView.adapter = TacheAdapter(it,this)
        })

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TODOViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onItemClick(position: Int) {
        var action = TODOFragmentDirections.actionTODOFragmentToEventDetailsFragment()
        action.setMyEventPosition(position)
        action.setMyEventID(MyEventsNotValidated.get(position).eventId)
        this.findNavController().navigate(action)
    }

}