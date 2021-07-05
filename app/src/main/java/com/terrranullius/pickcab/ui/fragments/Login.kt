package com.terrranullius.pickcab.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.terrranullius.pickcab.R
import com.terrranullius.pickcab.other.EventObserver
import com.terrranullius.pickcab.ui.viewmodels.MainViewModel
import com.terrranullius.pickcab.util.Resource

class Login : Fragment() {
    lateinit var mobile_number: EditText
    lateinit var inputLayout: TextInputLayout

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mobile_number = view.findViewById(R.id.login_mobile_number)
        inputLayout = view.findViewById(R.id.mobile_input_layout)

        if (viewModel.phonenumber != 0L){
            mobile_number.setText(viewModel.phonenumber.toString())
        }

        view.findViewById<Button>(R.id.login_proceed).setOnClickListener {

            val mobile = mobile_number.text.toString()
            if (TextUtils.isEmpty(mobile)) {
                mobile_number.error = "Enter Mobile Number";
                inputLayout.error = "Enter Mobile Number"
                return@setOnClickListener
            }
            if (!TextUtils.isDigitsOnly(mobile)) {
                inputLayout.error = "Invalid Mobile Number"
                return@setOnClickListener
            }
            if (mobile.length < 10 || mobile.contains(" ") || mobile.contains("+")) {
                inputLayout.error = "Please Enter 10 digit Mobile Number"
                return@setOnClickListener
            }

            mobile.toLongOrNull()?.let {
                viewModel.startVerification(it)
            } ?: return@setOnClickListener
        }

        setObservers()
    }

    private fun setObservers() {
        viewModel.verificationStartedEvent.observe(viewLifecycleOwner, EventObserver {

            findNavController().navigate(R.id.action_login_to_OTPConfirmation)
            })
        }
    }
