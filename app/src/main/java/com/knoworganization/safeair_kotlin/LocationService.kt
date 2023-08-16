package com.knoworganization.safeair_kotlin

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Date


class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var auth: FirebaseAuth

    // creating a variable for our
    // Firebase Database.
    private val database = Firebase.database("https://safeair-b0c14-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.reference

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        auth = Firebase.auth
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }




    @SuppressLint("SimpleDateFormat")
    private fun start(){
        val notification = NotificationCompat.Builder(this,"location")
            .setContentTitle("Tracking Location")
            .setContentText("Location : null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val currentUser = auth.currentUser
        var lat: Double
        var lng: Double


        locationClient.getLocationUpdates(1000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                Log.d("LocationService", "Got location: $location")
                 lat = location.latitude
                 lng = location.longitude
                val updatedNotification = notification.setContentText(
                    "Location : ($lat , $lng)"
                )
                if (currentUser != null){
                    val email = currentUser.email.toString()
                    val formatter = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
                    val date = Date()
                    val current = formatter.format(date)
                    val data = LocationData(email, lat, lng, "online", current)
                    myRef.child("locations").child(currentUser.uid).setValue(data)

                }
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)
        startForeground(1, notification.build())
    }
    @SuppressLint("SimpleDateFormat")
    private fun stop(){
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
        val currentUser = auth.currentUser
        if (currentUser != null){
            val data: String = "offline"
            myRef.child("locations").child(currentUser.uid).child("status").setValue(data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}