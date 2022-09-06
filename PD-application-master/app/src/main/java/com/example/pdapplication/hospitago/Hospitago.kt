package com.example.pdapplication.hospitago

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pdapplication.R
import com.example.pdapplication.mapActivities.MapActivity
import kotlinx.android.synthetic.main.activity_hospitago.*
class Hospitago : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospitago)

//        open the patient activity

        tv_use.setOnClickListener {
            startActivity(Intent(this , user_Main::class.java))
        }

//        open the hospital activity

        tv_hospital.setOnClickListener {
            startActivity(Intent(this , Login::class.java))
        }



    }

}
