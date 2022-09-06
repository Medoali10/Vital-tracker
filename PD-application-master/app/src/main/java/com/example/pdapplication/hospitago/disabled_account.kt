package com.example.pdapplication.hospitago

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pdapplication.MainActivity
import com.example.pdapplication.R
import kotlinx.android.synthetic.main.activity_disabled_account.*

class disabled_account : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disabled_account)

//         back to the main activity

        back_home.setOnClickListener {
            startActivity(Intent(this , MainActivity::class.java))
            finish()
        }

    }
}
