package com.example.pdapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pdapplication.hospitago.Hospitago
import com.example.pdapplication.lineChart.LineChartAcivity
import kotlinx.android.synthetic.main.activity_test_your_self.*
// import io.flutter.embedding.android.FlutterActivity


class TestYourSelf : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_your_self)

//        open the line chart

        btnLineChart.setOnClickListener{
            startActivity(Intent(this,
                LineChartAcivity::class.java))
        }

//      back to the main activity

        back_btn.setOnClickListener {
            startActivity(Intent(this , MainActivity::class.java))
        }

  /*      btnDoctor.setOnClickListener {
            startActivity(FlutterActivity.createDefaultIntent(this))
        }
   */
//        go to the HOPITALS activity

        btnHospital.setOnClickListener {
            startActivity(Intent(this , Hospitago::class.java))
        }
    }
}
