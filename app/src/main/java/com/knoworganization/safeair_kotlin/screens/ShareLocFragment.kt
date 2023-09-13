package com.knoworganization.safeair_kotlin.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.knoworganization.safeair_kotlin.R
import com.knoworganization.safeair_kotlin.api.APIInterface
import com.knoworganization.safeair_kotlin.api.RequestLogInDataModel
import com.knoworganization.safeair_kotlin.api.RequestLogoutDataModel
import com.knoworganization.safeair_kotlin.api.ResponseClass
import com.knoworganization.safeair_kotlin.api.ServiceBuilder
import com.knoworganization.safeair_kotlin.location.LocationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class ShareLocFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var isStart: Boolean = false
    private val database = Firebase.database("https://safeair-b0c14-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.reference
    private val db = Firebase.firestore
    private lateinit var logInTime: String
    private var count = 0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var loginLat: String
    private lateinit var loginLng: String
    private lateinit var latLngResult: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    @SuppressLint("CommitPrefEdits", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_share_loc, container, false)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
        auth = Firebase.auth
        val currentUser = auth.currentUser

        val cacheDir = context?.cacheDir
        val file = File(cacheDir, "true")
        try {
            file.forEachLine {
                if (it == "true"){
                    isStart = true
                }
            }
        }catch (_: Exception){
        }

        val formatter1 = SimpleDateFormat("dd-MMM-yyyy")
        val date1 = Date()
        val currentDate1 = formatter1.format(date1)
        val countFile = File(cacheDir, "0")
        try {
            countFile.forEachLine {
                count = it.toInt()
            }
        }catch (e: Exception){
            Log.v("error", "${e.message}")
        }
        val dateFile = File(cacheDir, "date")
        try {
            if (dateFile.length() == 0L){
                dateFile.writeText(currentDate1)
            }
            dateFile.forEachLine {
                if (it != currentDate1){
                    countFile.writeText("0")
                    count = 0
                    dateFile.writeText(currentDate1)
                }
                Log.v("error", it)
            }
            Log.v("sd", dateFile.name)
        }catch (e: Exception){
            Log.v("error", "${e.message}")
        }

        view.findViewById<Button>(R.id.start).setOnClickListener(View.OnClickListener {
            Intent(requireActivity().applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                activity?.startService(this)
            }
//            Login data save in database
            val formatter = SimpleDateFormat("dd-MMM-yyyy")
            val timeFormatter = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
            val date = Date()
            val currentDate = formatter.format(date)
            val currentTime = timeFormatter.format(date)
            logInTime = currentTime
//            val logInData = hashMapOf(
//                "logInTime" to currentTime,
//                "logOutTime" to null,
//                "email" to currentUser?.email.toString()
//            )
//            ============== API POST ====================
            if (ActivityCompat.checkSelfPermission(
                    requireActivity().applicationContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireActivity().applicationContext,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100
                )
            }
//      get Latitude and Longitude
            val location = fusedLocationProviderClient.lastLocation
            location.addOnSuccessListener {
                if (it != null) {
                    val textLatitude = it.latitude.toString()
                    val textLongitude = it.longitude.toString()
                    Log.v("TAG", textLatitude)
                    Log.v("TAG", textLongitude)
                    loginLat = textLatitude
                    loginLng = textLongitude
                    val retrofit= ServiceBuilder.buildService(APIInterface::class.java)
                    val obj = RequestLogInDataModel(
                        currentDate,
                        currentTime,
                        currentUser?.email.toString(),
                        count,
                        textLatitude,
                        textLongitude )
                    retrofit.requestSendLoginData(obj).enqueue(
                        object:Callback<ResponseClass>{
                            override fun onResponse(
                                call: Call<ResponseClass>,
                                response: Response<ResponseClass>
                            ) {
                                Log.v("TAG", response.body()?.message.toString())
                            }
                            override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                                Log.v("TAG", "${t.message}")                    }
                        }
                    )

                }
            }
//            =======================================
            file.writeText("true")
            isStart = true
            view.findViewById<Button>(R.id.start).isEnabled = false
            view.findViewById<Button>(R.id.stop).isEnabled = true
        })

        view.findViewById<Button>(R.id.stop).setOnClickListener(View.OnClickListener {
            Intent(requireActivity().applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                activity?.stopService(this)
            }
            if (currentUser != null) {
                val data: String = "offline"
                myRef.child("locations").child(currentUser.uid).child("status").setValue(data)
            }
//            Logout data save in database
            val formatter = SimpleDateFormat("dd-MMM-yyyy")
            val timeFormatter = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
            val date = Date()
            val currentDate = formatter.format(date)
            val currentTime = timeFormatter.format(date)

//            ============== API POST ====================
            Log.v("TAG", "$loginLng and $ & $loginLat")
            if (ActivityCompat.checkSelfPermission(
                    requireActivity().applicationContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireActivity().applicationContext,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100
                )
            }
//      get Latitude and Longitude
            val location = fusedLocationProviderClient.lastLocation
            location.addOnSuccessListener {
                if (it != null) {
                    val textLatitude = it.latitude.toString()
                    val textLongitude = it.longitude.toString()
                    Log.v("TAG", textLatitude)
                    Log.v("TAG", textLongitude)

                    val retrofit= ServiceBuilder.buildService(APIInterface::class.java)
                    val obj = RequestLogoutDataModel(
                        currentDate,
                        logInTime ,
                        currentTime,
                        currentUser?.email.toString(),
                        count,
                        loginLat,
                        loginLng,
                        textLatitude,
                        textLongitude )
                    retrofit.requestSendLogoutData(obj).enqueue(
                        object:Callback<ResponseClass>{
                            override fun onResponse(
                                call: Call<ResponseClass>,
                                response: Response<ResponseClass>
                            ) {
                                Log.v("TAG", response.body()?.message.toString())
                            }
                            override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                                Log.v("TAG", "${t.message}")                    }
                        }
                    )
                    count++
                    countFile.writeText("$count")
                }
            }
//            =======================================
            file.writeText("false")
            isStart = false
            view.findViewById<Button>(R.id.start).isEnabled = true
            view.findViewById<Button>(R.id.stop).isEnabled = false

        })

        if (isStart){
            view.findViewById<Button>(R.id.start).isEnabled = false
            view.findViewById<Button>(R.id.stop).isEnabled = true
        }else{
            view.findViewById<Button>(R.id.start).isEnabled = true
            view.findViewById<Button>(R.id.stop).isEnabled = false
        }

        view.findViewById<Button>(R.id.logout).setOnClickListener(View.OnClickListener {
            if (isStart){
                val toast = Toast.makeText(requireActivity().applicationContext, "Stop the Location first", Toast.LENGTH_SHORT)
                toast.show()
            }else{
                Firebase.auth.signOut()
                findNavController().popBackStack()
            }
        })
        return view
    }
}