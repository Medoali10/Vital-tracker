package com.example.pdapplication.hospitago


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color.red
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.pdapplication.R
import kotlinx.android.synthetic.main.activity_main_hospital.*
import java.util.*


class adapter_hospitals (private val cartlist: ArrayList<card>,
                      private val context: Context) : RecyclerView.Adapter<adapter_hospitals.MyViewHolder4>() {
    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(p0: adapter_hospitals.MyViewHolder4, p1: Int) {

        p0.name.text= cartlist[p1].username
        p0.position.text= cartlist[p1].position
        p0.adress.text= cartlist[p1].adress

//        add the call functionalitty

        p0.call.setOnClickListener {


            val intent = Intent(Intent.ACTION_DIAL);
            intent.data = Uri.parse("tel:"+ cartlist[p1].phone)

            p0.call.context.startActivity(intent)



        }
        p0.callperson.setOnClickListener {


            val intent = Intent(Intent.ACTION_DIAL);
            intent.data = Uri.parse("tel:"+ cartlist[p1].phone2)

            p0.call.context.startActivity(intent)



        }

    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder4 {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card, p0, false)

        return MyViewHolder4(view,context)
    }

    override fun getItemCount(): Int {
        return cartlist.size //subject.listItemsTxta.size
    }




    class MyViewHolder4(itemView: View,context:Context) : RecyclerView.ViewHolder(itemView) {

//        set the values of the Hospital Card


     var name= itemView.findViewById(R.id.hospital_name_card) as TextView
        var position= itemView.findViewById(R.id.hospital_position) as TextView
        var mood= itemView.findViewById(R.id.img_mood) as ImageView
        var call= itemView.findViewById(R.id.btn_call) as Button
        var callperson= itemView.findViewById(R.id.btn_call_person) as Button
        var adress= itemView.findViewById(R.id.hospital_adress) as TextView



    }

}