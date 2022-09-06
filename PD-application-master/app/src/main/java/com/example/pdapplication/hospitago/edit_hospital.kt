package com.example.pdapplication.hospitago

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.pdapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_hospital.*

class edit_hospital : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_hospital)

//         get the current user email

        mAuth = FirebaseAuth.getInstance()
        email = mAuth?.currentUser?.email.toString()

    }

//  save the new text fields values to the database


    fun edit(view: View) {

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("Hospital").child(SplitString(email.toString()))
        myRef.child("name").setValue(ed_edit_hospital_fullname.text.toString())
        myRef.child("position").setValue(ed_hospital_position.text.toString())
        myRef.child("phone").setValue(ed_edit_hospital_phone.text.toString())

        Toast.makeText(applicationContext, "Done.", Toast.LENGTH_LONG).show()

    }

    fun SplitString(email:String):String{
        val split= email.split("@")
        return split[0]
    }

}
