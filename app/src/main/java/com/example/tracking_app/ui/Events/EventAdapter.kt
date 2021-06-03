package com.example.tracking_app.ui.Events

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tracking_app.R
import com.example.tracking_app.database.models.EventEntity
import com.example.tracking_app.utils.toBitmap
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(private val events : List<EventEntity>,private val listner : OnItemClickListener) :  RecyclerView.Adapter<EventAdapter.ViewHolder>()  {

    private val description = arrayOf("descript ion1de scr iption1de scri ptio  n1desc riptio n1descr iption1de scrip tion1description1description1description1description1description1description1description1","description2","description3")
    private val title = arrayOf("title1","title2","title3")
    private val image = intArrayOf(R.drawable.yassine,R.drawable.ic_user,R.drawable.ic_user)
    private var filtredEvents : List<EventEntity>? = null
    private var ListAllEvents : List<EventEntity> = events

    //boite pour ranger tout les composants a controler
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view),View.OnClickListener {
        var ImageEvent = view.findViewById<ImageView>(R.id.ImageEvent)
        var Title : TextView? = view.findViewById(R.id.EventName)
        var EventDescription : TextView? = view.findViewById(R.id.EventDescription)
        var dataEvent : TextView? = view.findViewById(R.id.dataEvent)
        var MyView  = view
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listner.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_event,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventAdapter.ViewHolder, position: Int) {
        holder.EventDescription?.text = events.get(position).description
        holder.Title?.text= events.get(position).titre
        holder.ImageEvent.setImageBitmap(events.get(position).imageURL.toBitmap())
        holder.dataEvent?.text = toSimpleString(events.get(position).date)
        if(!events.get(position).valider){
            holder.MyView.setBackgroundColor(Color.parseColor("#EAF1EA"))
        }
        //loadImage(holder.ImageEvent,events.get(position).imageURL)
    }

    fun toSimpleString(date: Date?) = with(date ?: Date()) {
        //SimpleDateFormat("E, dd MMM yyyy HH:mm").format(this)
        SimpleDateFormat("E, dd MMM HH:mm").format(this)
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun FilterMyEvents(constraint : CharSequence? ,MyEvents :List<EventEntity> ) : List<EventEntity> {
        var filtredEvents : List<EventEntity>? = null
        val Key: String = constraint.toString()
        if (Key.isEmpty()) {
            filtredEvents = MyEvents
        } else {
            val lstFiltered: MutableList<EventEntity> = ArrayList<EventEntity>()
            for (row in MyEvents) {
                if (row.titre.toLowerCase().contains(Key.toLowerCase())) {
                    lstFiltered.add(row)
                }
            }
            filtredEvents = lstFiltered
        }
        return filtredEvents
    }


}