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
import com.google.firebase.ktx.Firebase
import java.io.File
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("CommitPrefEdits")
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
        file.forEachLine {
            isStart = it == "true"
        }

        view.findViewById<Button>(R.id.start).setOnClickListener(View.OnClickListener {
            Intent(requireActivity().applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                activity?.startService(this)
            }
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