package com.knoworganization.safeair_kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth

    private lateinit var email: String
    private lateinit var password: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SigninFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}