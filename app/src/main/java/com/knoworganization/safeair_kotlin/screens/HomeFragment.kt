package com.knoworganization.safeair_kotlin.screens

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        checkDeviceHasBiometric()
        executor = ContextCompat.getMainExecutor(requireActivity().applicationContext)
        biometricPrompt= BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    findNavController().navigate(R.id.goToLogin)
                }
            }
        )

        promptInfo= BiometricPrompt.PromptInfo.Builder()
            .setTitle("Continue to login")
            .setSubtitle("scan biometrics...")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        view.findViewById<Button>(R.id.login).setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        return view
    }
    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkDeviceHasBiometric(){
        val biometricManager = BiometricManager.from( requireActivity().applicationContext)
        when(biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)){
            BiometricManager.BIOMETRIC_SUCCESS ->{
                Log.d("TAG","App can authenticate using biometric")
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{
                Log.d("TAG","App can not authenticate using biometric")
                val lockIntent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                startActivity(lockIntent)

            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->{
                Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply{
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                val lockIntent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                startActivity(lockIntent)
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                TODO()
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                TODO()
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                TODO()
            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                TODO()
            }
        }
    }
}