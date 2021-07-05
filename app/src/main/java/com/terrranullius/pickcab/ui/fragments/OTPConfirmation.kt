package com.terrranullius.pickcab.ui.fragments

import android.content.Context
import androidx.cardview.widget.CardView
import android.os.Bundle
import com.terrranullius.pickcab.R
import android.text.TextWatcher
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.terrranullius.pickcab.other.Constants.PREFS_DIR
import com.terrranullius.pickcab.other.Constants.PREF_NUMBER
import com.terrranullius.pickcab.other.Constants.PREF_VERIFIED
import com.terrranullius.pickcab.other.EventObserver
import com.terrranullius.pickcab.ui.viewmodels.MainViewModel
import com.terrranullius.pickcab.util.Resource

class OTPConfirmation : Fragment(), View.OnClickListener {
    lateinit var otp1: EditText
    lateinit var otp2: EditText
    lateinit var otp3: EditText
    lateinit var otp4: EditText
    lateinit var otp5: EditText
    lateinit var otp6: EditText
    lateinit var verify: Button
    lateinit var edit: ImageButton
    lateinit var mobile: TextView
    lateinit var invalidotp: TextView
     var sMob: String = " "
    lateinit var verifyCard: CardView
    lateinit var resendOTP: TextView

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_otpconfirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sMob = viewModel.phonenumber.toString()

        mobile = view.findViewById(R.id.otp_mobile)
        verify = view.findViewById(R.id.verify_otp)
        resendOTP = view.findViewById(R.id.resend_otp)
        otp1 = view.findViewById(R.id.otp_pin_1)
        otp2 = view.findViewById(R.id.otp_pin_2)
        otp3 = view.findViewById(R.id.otp_pin_3)
        otp4 = view.findViewById(R.id.otp_pin_4)
        otp5 = view.findViewById(R.id.otp_pin_5)
        otp6 = view.findViewById(R.id.otp_pin_6)
        edit = view.findViewById(R.id.edit_mobile)
        invalidotp = view.findViewById(R.id.invalid_otp)
        verifyCard = view.findViewById(R.id.verify_card)
        mobile.text = sMob
        otp1.addTextChangedListener(EditTextWatcher(otp1))
        otp2.addTextChangedListener(EditTextWatcher(otp2))
        otp3.addTextChangedListener(EditTextWatcher(otp3))
        otp4.addTextChangedListener(EditTextWatcher(otp4))
        otp5.addTextChangedListener(EditTextWatcher(otp5))
        otp6.addTextChangedListener(EditTextWatcher(otp6))
        verify.setOnClickListener(this)
        edit.setOnClickListener(this)
        
        setObservers()
    }

    private fun setObservers() {
        viewModel.otpSetEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is Resource.Success -> {
                    onOtpVerified()
                }
                is Resource.Error -> {

                }
                Resource.Loading -> {

                }
            }
        })
    }

    private fun onOtpVerified() {
        val prefs = requireContext().getSharedPreferences(PREFS_DIR, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(PREF_VERIFIED, true)
        editor.putLong(PREF_NUMBER, viewModel.phonenumber)
        editor.apply()
        findNavController().navigate(R.id.action_OTPConfirmation_to_mainFragment)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.edit_mobile -> findNavController().navigate(R.id.action_OTPConfirmation_to_login)
            R.id.verify_otp -> viewModel.verifyOtp(oTP?.toIntOrNull() ?: return)
            R.id.resend_otp -> viewModel.startVerification(viewModel.phonenumber)
            else -> {
            }
        }
    }

    inner class EditTextWatcher(var view: View?) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            invalidotp.visibility = View.GONE
            val text = editable.toString()
            when (view!!.id) {
                R.id.otp_pin_1 -> if (text.length == 1) {
                    otp2.requestFocus()
                }
                R.id.otp_pin_2 -> if (text.length == 1) {
                    otp3.requestFocus()
                } else {
                    otp1.requestFocus()
                }
                R.id.otp_pin_3 -> if (text.length == 1) {
                    otp4.requestFocus()
                } else {
                    otp2.requestFocus()
                }
                R.id.otp_pin_4 -> if (text.length == 1) {
                    otp5.requestFocus()
                } else {
                    otp3.requestFocus()
                }
                R.id.otp_pin_5 -> if (text.length == 1) {
                    otp6.requestFocus()
                } else {
                    otp4.requestFocus()
                }
                R.id.otp_pin_6 -> if (text.length != 1) {
                    otp5.requestFocus()
                }
                else -> {
                }
            }
            if (oTP != null && oTP!!.length == 6) {
                verify.isEnabled = true
                verifyCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                verify.performClick()
            } else {
                verify.isEnabled = false
                verifyCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey1))
            }
        }
    }

    val oTP: String?
        get() {
            return otp1.text.toString() + otp2.text.toString() + otp3.text.toString() + otp4.text.toString() + otp5.text.toString() + otp6.text.toString()
        }

    companion object {
        private const val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"
    }
}