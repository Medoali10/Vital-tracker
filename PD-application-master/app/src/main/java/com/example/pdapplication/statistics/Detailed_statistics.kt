package com.example.pdapplication.statistics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pdapplication.R
import kotlinx.android.synthetic.main.activity_detailed_statistics.*
import org.json.JSONObject

class Detailed_statistics : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_statistics)

// shift to the slider

        why_info.setOnClickListener {
            startActivity(Intent(this, Slider_info::class.java))
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

                txtUpdated.text = jsonObject.getString("updated")
                txtTests.text = jsonObject.getString("tests")
                txtActive.text = jsonObject.getString("active")
                txtCritical.text = jsonObject.getString("critical")
                txtAffected.text = jsonObject.getString("affectedCountries")
            },
            Response.ErrorListener {

//                something went wrong

                Toast.makeText(this , "someting went wrong..." , Toast.LENGTH_LONG).show()
                txtUpdated.text = "-"
                txtTests.text = "-"
                txtActive.text = "-"
                txtCritical.text = "-"
                txtAffected.text = "-"
            })
        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}
