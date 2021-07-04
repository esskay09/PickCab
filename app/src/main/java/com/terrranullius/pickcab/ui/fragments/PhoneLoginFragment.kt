package com.terrranullius.pickcab.ui.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.terrranullius.pickcab.R
import com.terrranullius.pickcab.databinding.FragmentPhoneLoginBinding
import com.terrranullius.pickcab.other.EventObserver
import com.terrranullius.pickcab.ui.viewmodels.MainViewModel


class PhoneLoginFragment : Fragment() {

    lateinit var binding: FragmentPhoneLoginBinding
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_phone_login, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textInputLayout.editText?.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnKeyListener setPhoneNumber()
            } else return@setOnKeyListener false
        }

        binding.buttonPhoneLogin.setOnClickListener {
            setPhoneNumber()
        }
        setObservers()
    }

    private fun setPhoneNumber(): Boolean {

        val number = binding.textInputLayout.editText?.text?.toString()?.filter {
            !it.isWhitespace()
        }
        return if (number?.length == 10
            || number?.length == 12
        ) {
            viewModel.phonenumber = number.toLong()
            true
        } else {
            binding.textInputLayout.error = "Please enter a correct number"
            false
        }
        }

    private fun setObservers() {
        viewModel.verificationStartEvent.observe(viewLifecycleOwner, EventObserver {
//            findNavController().navigate(R.id.action_phoneLoginFragment_to_mainFragment)
        })
    }
}