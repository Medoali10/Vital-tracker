package com.example.pdapplication.hospitago

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pdapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login.*


class Login : AppCompatActivity() {

    var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        auth=FirebaseAuth.getInstance()

//        shift to the register activity

        registerBtn.setOnClickListener {
            val intent = Intent(this, hospital_registration::class.java)
            startActivity(intent)
        }

//      login action

        loginBtn.setOnClickListener {

            var email = edusername.text.toString().trim()
            var password = edpassword.text.toString().trim()

//            validations

            if(email.isEmpty()){
                edusername.error = "Email required"
                edusername.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edusername.error = "Valid email required"
                edusername.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty() || password.length < 6){
                edpassword.error = "6 char password required"
                edpassword.requestFocus()
                return@setOnClickListener
            }
            loginUser(email , password)
        }

    }


   private fun loginUser(email: String , password : String){

//       sign in the user

       auth?.signInWithEmailAndPassword(email , password)
           ?.addOnCompleteListener(this){task ->
               if(task.isSuccessful){
                   val intent = Intent(this , main_hospital::class.java).apply {
                   flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                   }
                   intent.putExtra("email" , email)

//        shift the main hospital activity
                    startActivity(intent)
               }else{
                    task.exception?.message?.let {
                        Toast.makeText(this , it , Toast.LENGTH_LONG).show()
                    }
               }
           }

   }


    }
