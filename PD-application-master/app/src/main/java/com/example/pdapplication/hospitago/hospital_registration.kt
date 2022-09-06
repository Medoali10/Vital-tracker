package com.example.pdapplication.hospitago

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pdapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.fragment_hospital_registration.*
import kotlinx.android.synthetic.main.fragment_hospital_registration.ed_hospital_position
import java.text.SimpleDateFormat
import java.util.*


class hospital_registration : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_hospital_registration)

        mAuth = FirebaseAuth.getInstance()

    }


// register the HOSPITALS with their data

    fun registration2(view: View) {

//        get the hospital data

        var email: String = ed_hospital_username.text.toString()
        var password: String = ed_hospital_password.text.toString()
        var phone: String = ed_hospital_phone.text.toString()
        var personphone: String = ed_hospital_perosnphone.text.toString()
        var name: String = ed_hospital_fullname.text.toString()
        var position: String = ed_hospital_position.text.toString()
        var adress: String = ed_hospital_adress.text.toString()



        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference().child("Hospital").child(SplitString(email))

        if (isValet()) {

//            register the hospital

            mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this
                ) { task ->
                    if (task.isSuccessful) {

                        val user = mAuth!!.currentUser
                        val df = SimpleDateFormat("dd-MM-yy-HH-mm-ss")
                        val dataobj = Date()
                        var dateFinal: String = df.format(dataobj).toString()


//            save the hospital data to the database

                        myRef.child("name").setValue(name.toString())
                        myRef.child("time").setValue(dateFinal.toString())
                        myRef.child("CurrentUser").setValue(user.toString())
                        myRef.child("email").setValue(email.toString())
                        myRef.child("password").setValue(password.toString())
                        myRef.child("phone").setValue(phone.toString())
                        myRef.child("adress").setValue(adress.toString())
                        myRef.child("position").setValue(position.toString())
                        myRef.child("personphone").setValue(personphone.toString())
                        myRef.child("mood").setValue("available")

                        val   myRef2 = database.reference.child("types").child(SplitString(email))
                        myRef2.child("types").setValue("hospital")


                        Toast.makeText(
                            applicationContext,
                            "Done.",
                            Toast.LENGTH_LONG
                        ).show()

//                        shift to the login activity

                        val intent = Intent(applicationContext, Login::class.java)
                        startActivity(intent)

                        finish()
                    } else {

                        Toast.makeText(
                            applicationContext,
                            "Authentication failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                applicationContext,
                "please enter all fields",
                Toast.LENGTH_LONG
            )

        }
    }

    fun SplitString(email:String):String{
        val split= email.split("@")
        return split[0]
    }

    private fun isValet(): Boolean {
        return  true
    }

//    show the options for the hospital employer AMBULANCE or DOCTOR

    fun showPopup2(view: View) {

    val popup = PopupMenu(applicationContext, view)
    val inflater: MenuInflater = popup.menuInflater
    inflater.inflate(R.menu.actions_reg, popup.menu)

    popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
        when(item.itemId) {
            R.id.btn_ch1->
            {
                ed_hospital_position.setText("doctor")

            }
            R.id.btn_ch2->
            {
                ed_hospital_position.setText("ambulance")

            }


        }
        true
    })

    popup.show()

}}
