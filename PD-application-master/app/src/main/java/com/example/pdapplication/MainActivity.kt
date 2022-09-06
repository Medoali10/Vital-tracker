package com.example.pdapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pdapplication.mapActivities.MapActivity
import com.example.pdapplication.statistics.Statistics
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val REQUEST_CALL = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        click the bell to call the map

        bell_urgent.setOnClickListener {
            startActivity(Intent(this , MapActivity::class.java))
        }

        // OneSignal Initialization to send notification to all users

        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()


//        shift to the statistics activity

        stat.setOnClickListener(View.OnClickListener {
            val i = Intent(this@MainActivity, Statistics::class.java)
            startActivity(i)
        })

//         call 123 function
        callnow.setOnClickListener {
           makePhoneCall()
        }

//        open to LINE Chart and doctor droid

        testYourseld.setOnClickListener {
            startActivity(Intent(this , TestYourSelf::class.java))
        }

    }

//    ask for permissions and PHONE call function

    private fun makePhoneCall() {
        val number: String = "123"
        if (number.trim { it <= ' ' }.length > 0) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_CALL
                )
            } else {
                val dial = "tel:$number"
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            }
        } else {
            Toast.makeText(this@MainActivity, "Enter Phone Number", Toast.LENGTH_SHORT).show()
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