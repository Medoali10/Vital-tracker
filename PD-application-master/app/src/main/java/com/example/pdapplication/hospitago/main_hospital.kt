package com.example.pdapplication.hospitago

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.example.pdapplication.R
import com.example.pdapplication.TestYourSelf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main_hospital.*

class main_hospital : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_hospital)

        var mProgressDialog: ProgressDialog = ProgressDialog(this);
        mProgressDialog.setMessage("Work ...");
        var email = intent.getStringExtra("email")//=="available"

//        get the data from the database

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference().child("Hospital").child(SplitString(email))
        val myRef2 = database.getReference().child("types").child(SplitString(email))

//      disable your account

        btn_change.setOnClickListener {
            myRef.removeValue()
            myRef2.removeValue()
            startActivity(Intent(this , disabled_account::class.java))
                finish()
        }

//        shift to the edit activity

        btn_edit.setOnClickListener {
            startActivity(Intent(this , edit_hospital::class.java))
        }

//        back to TEST_YOURSELF activity

        back_btn.setOnClickListener {
            startActivity(Intent(this , TestYourSelf::class.java ))
            finish()
        }
    }

    fun SplitString(email:String):String{
        val split= email.split("@")
        return split[0]
    }

}
