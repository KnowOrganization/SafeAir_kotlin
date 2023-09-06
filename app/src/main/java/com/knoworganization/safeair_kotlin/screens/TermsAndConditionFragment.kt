package com.knoworganization.safeair_kotlin.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.knoworganization.safeair_kotlin.R
import java.io.File

class TermsAndConditionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_terms_and_condition, container, false)

        val cacheDir = context?.cacheDir
        val agreeFile = File(cacheDir, "agree")
        try {
            agreeFile.forEachLine {
                if (it == "true"){
                    findNavController().navigate(R.id.goToHome)
                }
            }
        }catch (_: Exception){
        }


        view.findViewById<Button>(R.id.agree).setOnClickListener {
            agreeFile.writeText("true")
            findNavController().navigate(R.id.goToHome)
        }

        view.findViewById<Button>(R.id.disagree).setOnClickListener {
            activity?.moveTaskToBack(true)
            activity?.finish()
        }

        return view
    }

}