package com.example.pdapplication.lineChart

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pdapplication.R

class TempAdapter(val tempList : ArrayList<Temp>) : RecyclerView.Adapter<TempAdapter.viewHolder>() {

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val temp : Temp = tempList[position]

        holder.textViewDate.text = temp.date
        holder.textViewTemp.text = temp.temp
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.temp_list , parent , false)
        return viewHolder(v)
    }

    override fun getItemCount(): Int {
        return tempList.size
    }
    class viewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val textViewDate = itemView.findViewById(R.id.date) as TextView
        val textViewTemp = itemView.findViewById(R.id.temp) as TextView


    }

}