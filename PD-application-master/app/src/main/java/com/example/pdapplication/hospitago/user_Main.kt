package com.example.pdapplication.hospitago

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdapplication.R
import com.example.pdapplication.mapActivities.MapActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_user__main.*

class user_Main : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user__main)


//        read and display the hospitals in the activity

        read_hospitals()

//        open the nearby place activity

        open_map.setOnClickListener {
            startActivity(Intent(this , MapActivity::class.java))
        }
        
    }

    fun read_hospitals(){
        listhospitals.clear()
        var mProgressDialog: ProgressDialog = ProgressDialog(this);
        mProgressDialog.setMessage("Work ...");
        mProgressDialog.show();

//        get the data from DATABASE

        var db = FirebaseDatabase.getInstance().getReference().child("Hospital")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                try {
                    if (p0.exists()){
                        listhospitals .clear()

                        var nour = p0!!.value!! as HashMap<String?, Any?>
                        for (key in nour.keys) {
                            var data = nour[key] as HashMap<String?, Any?>

//                            add the hospitals to the recycler view

                            listhospitals.add(
                                card(data["name"].toString(),data["phone"].toString(),
                                    data["mood"].toString(),data["position"].toString()
                                ,data["adress"].toString(),data["personphone"].toString()))
                            }
                        mProgressDialog.dismiss();

//                        customize and adapt the recycler view

                        var rv=findViewById<RecyclerView>(R.id.rechospital)
                        val layoutManager = LinearLayoutManager(applicationContext)
                        rv.layoutManager = layoutManager
                        rv.adapter = adapter_hospitals(listhospitals,applicationContext)

                    } else {

//                        if the database is empty

                        listhospitals .clear()
                        var rv=findViewById<RecyclerView>(R.id.rechospital)
                        val layoutManager = LinearLayoutManager(applicationContext)
                        rv.layoutManager = layoutManager
                        rv.adapter = adapter_hospitals(listhospitals,applicationContext)
                        Toast.makeText(getApplicationContext(), "can't find any hospital.!",
                            Toast.LENGTH_LONG).show();

                        mProgressDialog.dismiss();

                    }

                } catch (ex: Exception) {

                    listhospitals.clear()
                    var rv=findViewById<RecyclerView>(R.id.rechospital)
                    val layoutManager = LinearLayoutManager(applicationContext)
                    rv.layoutManager = layoutManager
                    rv.adapter = adapter_hospitals(listhospitals,applicationContext)
                    Toast.makeText(applicationContext, ex.message.toString(), Toast.LENGTH_LONG).show()

                }


            }

            override fun onCancelled(p0: DatabaseError) {
            }


        })


    }
    companion object{

        var listhospitals = ArrayList<card>()

    }
}
