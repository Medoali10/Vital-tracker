package com.example.pdapplication.lineChart

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pdapplication.R
import com.example.pdapplication.hospitago.user_Main
import kotlinx.android.synthetic.main.activity_high_temprature_activity.*

class High_temprature_activity : AppCompatActivity() {

    private val REQUEST_CALL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_temprature_activity)

//        get the alert message and display it

        var message = intent.getStringExtra("message")

        text_view.text = message

//        call the emergency
        emergency.setOnClickListener {
            makePhoneCall()
        }

//        find a hospital

        find_hospital.setOnClickListener {
            startActivity(Intent(this , user_Main::class.java))
        }

    }


    private fun makePhoneCall() {
        val number: String = "123"
        if (number.trim { it <= ' ' }.length > 0) {
            if (ContextCompat.checkSelfPermission(
                    this@High_temprature_activity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@High_temprature_activity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_CALL
                )
            } else {
                val dial = "tel:$number"
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            }
        } else {
            Toast.makeText(this@High_temprature_activity, "Enter Phone Number", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                invoke the call function

                makePhoneCall()
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
