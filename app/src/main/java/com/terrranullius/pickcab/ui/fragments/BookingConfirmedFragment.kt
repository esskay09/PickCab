package com.terrranullius.pickcab.ui.fragments

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.terrranullius.pickcab.R
import com.terrranullius.pickcab.databinding.FragmentBookingConfirmedBinding
import com.terrranullius.pickcab.network.ConfirmationRequest
import com.terrranullius.pickcab.network.PickCabApi
import com.terrranullius.pickcab.ui.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookingConfirmedFragment : Fragment() {

    private lateinit var binding: FragmentBookingConfirmedBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_booking_confirmed, container, false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBookingDetails()
        viewModel.sendConfirmation()

        lifecycleScope.launch(){
                delay(200L)
                tickAnimation()
        }


        binding.finishButton.setOnClickListener {
            findNavController().navigate(R.id.action_bookingConfirmedFragment_to_mainFragment)
        }
    }

    private fun setBookingDetails() {
        binding.fromDetailTv.text = viewModel.startDestination
        binding.toDetailTv.text = viewModel.endDestination
        binding.oneWayDetailTv.text = if (viewModel.oneWay) "Yes" else "No"
        binding.dateDetailTv.text =
            if (viewModel.oneWay) viewModel.startDate else "${viewModel.startDate} to ${viewModel.endDate}"
        binding.pickupTimeDetailTv.text = viewModel.time
    }


    private fun tickAnimation() {
        val drawable = binding.tickAnimationView.drawable
        if (drawable is AnimatedVectorDrawable) {
            val anim = drawable as AnimatedVectorDrawable
            anim.start()
        } else if (drawable is AnimatedVectorDrawableCompat) {
            val anim = drawable as AnimatedVectorDrawableCompat
            anim.start()
        }
    }
}