package com.example.pdapplication.lineChart

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pdapplication.R
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_line_chart_acivity.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Float.parseFloat
import java.security.KeyStore
import java.util.ArrayList

class LineChartAcivity : AppCompatActivity() {

// the API link

    private var JSON_URL = "https://api.thingspeak.com/channels/1076457/feeds.json?api_key=6AYAID1JBVXLB0LQ&results=20"


//    declare the notification channel id

    var CHANNEL_ID = "personal_notifications"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_chart_acivity)


//         initialize the data Array list

        var   data = ArrayList<Entry>()

//       initialize Recycler view to display the temprature

        val recyclerView = findViewById<RecyclerView>(R.id.temps_view)

        recyclerView.layoutManager = LinearLayoutManager(this , RecyclerView.VERTICAL , false)

        val temps = ArrayList<Temp>()
        // fetch the data from ThingSpeak APi
        var stringRequest = StringRequest(Request.Method.GET , JSON_URL ,  Response.Listener<String> {
            try {
                var obj = JSONObject(it)
                var dataArray = obj.getJSONArray("feeds")

                for (i in 0..dataArray.length()) {

//                    get the data object ( the day , the temprature)

                    var dataObject = dataArray.getJSONObject(i)
                    var dataX = dataObject.getString("created_at")[11].toString() + dataObject.getString("created_at")[12].toString()
                    var dataY = dataObject.getString("field1")

//                    add the data to the recycler view
                    var full_date = dataObject.getString("created_at")
                    temps.add(Temp(full_date , dataY))
                    val adapter = TempAdapter(temps)

                    recyclerView.adapter = adapter
//                  invoke the notification

                    if(parseFloat(dataY) > 35.0) {

                        createNotification(dataY)

                    }
//                    add the data to the LineDataSet and setup the line chart

                    data.add(Entry(parseFloat(dataX), parseFloat(dataY)))
                    var lineDataSet = LineDataSet(data , "your Temprature")

                    var iLineDataSets = ArrayList<LineDataSet>()
                    iLineDataSets.add(lineDataSet)

                    var lineData = LineData(lineDataSet)
                    lineChart.setData(lineData)

//                 customize the line chart

                    lineDataSet.color = Color.GRAY
                    lineDataSet.setDrawCircles(true)
                    lineDataSet.setCircleColor(Color.GRAY)
                    lineDataSet.circleHoleColor = Color.BLACK
                    lineDataSet.setDrawCircleHole(true)
                    lineDataSet.lineWidth = 5F
                    lineDataSet.circleRadius = 10F
                    lineDataSet.valueTextSize = 10F
                    lineDataSet.valueTextColor = Color.BLACK
                }

            }catch (e:JSONException){
                e.printStackTrace()
            }},
            Response.ErrorListener {

//                if the request failed

                Toast.makeText(this , it.message , Toast.LENGTH_LONG).show()
            })

//        add our request to the QUEUE
        var requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)

//        the rest of recycler view (Adapter)



//  revoke the line chart

        lineChart.invalidate()



//        set a background color

        lineChart.setBackgroundColor(Color.WHITE)

//        set text if the data are not available

        lineChart.setNoDataText("Data are not available")



    }

// build the notification

    private fun createNotification(data: String){

        var temp = parseFloat(data)

        createNotificationChannel()
        var message = "Your temprature is ${temp}!!"
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            this, CHANNEL_ID
        ).setSmallIcon(R.drawable.ic_add_alert)
            .setContentTitle("temprature Alert")
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        var intent = Intent(this , High_temprature_activity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("message" , message)

        var pendingIntent = PendingIntent.getActivity( this@LineChartAcivity , 0 , intent , PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        var notificationManagerCompat = NotificationManagerCompat.from(this)

        notificationManagerCompat.notify(0 , builder.build())
    }

//    create the notification channel which is required by 8.0 or higher android SDKs

    private fun createNotificationChannel (){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var name : CharSequence = "Personal Notifications"
            var description = "Include all the presonal notifications"
            var importance : Int = NotificationManager.IMPORTANCE_DEFAULT

            var notificationChannel = NotificationChannel(CHANNEL_ID , name , importance)
            notificationChannel.description = description

            var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
