package com.knoworganization.safeair_kotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS,
                ),
                0
            )
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Allow permissions to SafeAir")
                builder.setMessage("Allow Location permission to all the time")
                builder.setPositiveButton("Yes") { dialog, which ->
                    val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.setData(uri)
                    startActivity(intent)
                }
                builder.show()
            }
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ),
                0
            )
        }
//        checkDeviceHasBiometric()
//        executor = ContextCompat.getMainExecutor(this)
//        biometricPrompt= androidx.biometric.BiometricPrompt(
//            this,
//            executor,
//            object : BiometricPrompt.AuthenticationCallback() {
//                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                    super.onAuthenticationSucceeded(result)
//
//                }
//
//                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//                    super.onAuthenticationError(errorCode, errString)
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Authentication Error: $errString ",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        )
//
//        promptInfo= BiometricPrompt.PromptInfo.Builder()
//            .setTitle("title")
//            .setSubtitle("subtitle")
//            .setNegativeButtonText("Negative button text")
//            .build()
//
//        biometricPrompt.authenticate(promptInfo)
        setContentView(R.layout.activity_main)

    }
    private fun checkDeviceHasBiometric(){
        val biometricManager = BiometricManager.from( this)
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

                startActivityForResult(enrollIntent, 100)
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