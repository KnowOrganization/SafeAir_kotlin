package com.knoworganization.safeair_kotlin.screens

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.knoworganization.safeair_kotlin.R
import java.util.concurrent.Executor

class HomeFragment : Fragment() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        checkDeviceHasBiometric()
        executor = ContextCompat.getMainExecutor(requireActivity().applicationContext)
        biometricPrompt= androidx.biometric.BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    findNavController().navigate(R.id.goToLogin)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }
            }
        )

        promptInfo= BiometricPrompt.PromptInfo.Builder()
            .setTitle("Continue to login")
            .setSubtitle("scan biometrics...")
            .setNegativeButtonText("close")
            .build()

        view.findViewById<Button>(R.id.login).setOnClickListener(View.OnClickListener {
            biometricPrompt.authenticate(promptInfo)
        })

        return view
    }
    private fun checkDeviceHasBiometric(){
        val biometricManager = BiometricManager.from( requireActivity().applicationContext)
        when(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)){
            BiometricManager.BIOMETRIC_SUCCESS ->{
                Log.d("TAG","App can authenticate using biometric")
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{
                Log.d("TAG","App can not authenticate using biometric")


            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->{
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply{
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                }
                Toast.makeText(requireActivity().applicationContext, "No fingerprint enrolled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}