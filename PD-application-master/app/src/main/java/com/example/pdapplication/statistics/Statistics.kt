package com.example.pdapplication.statistics

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pdapplication.MainActivity
import com.example.pdapplication.R
import kotlinx.android.synthetic.main.activity_statistics.*
import org.json.JSONObject

class Statistics : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

//        shift to DOCTOR DROID activity

        bell_urgent.setOnClickListener(View.OnClickListener {
            val i = Intent(this@Statistics, MainActivity::class.java)
            startActivity(i)
            finish()
        })

//        shift to detailed statistics

        see_more.setOnClickListener {
            startActivity(Intent(this , Detailed_statistics::class.java))
        }


        getGlobalData()
    }
    fun getGlobalData(){
        var url = "https://disease.sh/v2/all"
        var stringRequest: StringRequest = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> {
                var jsonObject = JSONObject(it.toString())

//                set values to textViews
                txtConfirmed.text = jsonObject.getString("cases")
                txtRecovered.text = jsonObject.getString("recovered")
                txtDeaths.text = jsonObject.getString("deaths")
            },
            Response.ErrorListener {

//                something went wrong
                Toast.makeText(this , "someting went wrong..." , Toast.LENGTH_LONG).show()
                txtConfirmed.text = "-"
                txtRecovered.text = "-"
                txtDeaths.text = "-"
            })
        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}
