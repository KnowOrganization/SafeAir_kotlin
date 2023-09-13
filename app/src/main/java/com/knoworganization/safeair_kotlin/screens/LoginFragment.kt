package com.knoworganization.safeair_kotlin.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.knoworganization.safeair_kotlin.R

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        arguments?.let {
        }
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            if ((currentUser.email?.length ?: Int) != 0){
                findNavController().navigate(R.id.goToShareLoc)
            }
        }
//        view.findViewById<EditText>(R.id.email).setOnFocusChangeListener { view, focused ->
//            if (focused){
//                email = view.findViewById<EditText>(R.id.email).text.toString()
//                validateEmail(email)
//            }
//
//        }
        view.findViewById<Button>(R.id.loginBtn).setOnClickListener {
            email = view.findViewById<EditText>(R.id.email).text.toString()
            password = view.findViewById<EditText>(R.id.password).text.toString()
            view.findViewById<TextInputLayout>(R.id.emailTextInputLayout).helperText = ""
            view.findViewById<TextInputLayout>(R.id.passwordTextInputLayout).helperText = ""

            if (email != "" && password != ""){
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            findNavController().navigate(R.id.goToShareLoc)
                            Log.d("signin", "signInWithEmail:success")
                        } else {
                            // If sign in fails, display a message to the user.
//                        val toast = Toast.makeText(requireActivity().applicationContext, "Invalid Credentials", Toast.LENGTH_SHORT)
//                        toast.show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        val toast = Toast.makeText(requireActivity().applicationContext, "Error: ${exception.message}", Toast.LENGTH_SHORT)
                        toast.show()
                        Log.v("sd", exception.cause.toString())
                        view.findViewById<TextInputLayout>(R.id.emailTextInputLayout).helperText = "Invalid"
                        view.findViewById<TextInputLayout>(R.id.passwordTextInputLayout).helperText = "Invalid"
                    }
            }
            else{
                if (email == ""){
                    view.findViewById<TextInputLayout>(R.id.emailTextInputLayout).helperText = "Required"
                }
                if (password ==""){
                    view.findViewById<TextInputLayout>(R.id.passwordTextInputLayout).helperText = "Required"
                }
            }
        }
        return view
    }
}