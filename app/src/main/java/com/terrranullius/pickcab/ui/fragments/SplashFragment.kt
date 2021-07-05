package com.terrranullius.pickcab.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.terrranullius.pickcab.R
import com.terrranullius.pickcab.other.Constants
import com.terrranullius.pickcab.other.Constants.PREF_VERIFIED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("Users", Context.MODE_PRIVATE)
        val isVerified = prefs.getBoolean(PREF_VERIFIED, false)

        lifecycleScope.launch {
            delay(1000L)
            if (!isVerified)findNavController().navigate(R.id.action_splashFragment_to_login)
            else findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
        }

    }
}