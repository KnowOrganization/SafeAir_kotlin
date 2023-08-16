package com.knoworganization.safeair_kotlin

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.play.core.integrity.b
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.prefs.Preferences


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShareLocFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShareLocFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private var isStart: Boolean = false
    private val database = Firebase.database("https://safeair-b0c14-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.reference
    private val db = Firebase.firestore
    private lateinit var logInTime: String
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("CommitPrefEdits", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_share_loc, container, false)

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
            val logInData = hashMapOf(
                "logInTime" to currentTime,
                "logOutTime" to null,
                "email" to currentUser?.email.toString()
            )
            if (currentUser != null) {
                db.collection("Login/${currentUser.email}/sessions/$currentDate/session")
                    .document(count.toString()).set(logInData)
//                    .addOnSuccessListener { documentReference ->
//                        Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
//                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                    }
                myRef.child("LoginData").child(currentUser.uid).child("logInTime").setValue(currentTime)
                myRef.child("LoginData").child(currentUser.uid).child("email").setValue(currentUser.email.toString())

            }
//            =============
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
            val logOutData = hashMapOf(
                "logInTime" to logInTime,
                "logOutTime" to currentTime,
                "email" to currentUser?.email.toString()
            )
            if (currentUser != null) {
                db.collection("Login/${currentUser.email}/sessions/$currentDate/session")
                    .document(count.toString()).set(logOutData)
//                    .addOnSuccessListener { documentReference ->
//                        Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
//                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                    }
                myRef.child("LoginData").child(currentUser.uid).child("logOutTime").setValue(currentTime)
                count++
                countFile.writeText("$count")
            }

//            =================
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShareLocFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShareLocFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}