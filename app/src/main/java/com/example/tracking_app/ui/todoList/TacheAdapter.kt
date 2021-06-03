package com.example.tracking_app.ui.todoList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tracking_app.R
import com.example.tracking_app.database.models.EventEntity
import com.example.tracking_app.network.models.TacheItems


class TacheAdapter(private val events : List<TacheItems>, private val listner : OnItemClickListener) :  RecyclerView.Adapter<TacheAdapter.ViewHolder>()  {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var TitreTache = view.findViewById<TextView>(R.id.titreTache)


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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TacheAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.tacheitem,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.TitreTache.text = events.get(position).titre
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

}

