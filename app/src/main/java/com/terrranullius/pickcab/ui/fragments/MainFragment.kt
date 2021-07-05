package com.terrranullius.pickcab.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.terrranullius.pickcab.R
import com.terrranullius.pickcab.databinding.DialogOneWayPickerBinding
import com.terrranullius.pickcab.databinding.FragmentMainBinding
import com.terrranullius.pickcab.other.Constants.ADMIN_NUMBER
import com.terrranullius.pickcab.other.EventObserver
import com.terrranullius.pickcab.other.IdentitySource
import com.terrranullius.pickcab.ui.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private lateinit var btmDialog: BottomSheetDialog
    private lateinit var binding: FragmentMainBinding
    private lateinit var currentPhotoPath: String
    private val viewModel by activityViewModels<MainViewModel>()

    private lateinit var cameraLauncher: ActivityResultLauncher<Void>
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){

        }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()){

        }

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnContactUsPhone.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("tel:$ADMIN_NUMBER")
                startActivity(this)
            }
        }

        binding.btnContactUsWhatsapp.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=+91$ADMIN_NUMBER")
                startActivity(this)
            }
        }

        binding.inputEndDestination.editText?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnKeyListener setDestinations()
            } else return@setOnKeyListener false
        }

        binding.bookButton.setOnClickListener {
            setDestinations()
        }
        setObservers()
    }

    private fun setObservers() {
        viewModel.destinationsSetEvent.observe(viewLifecycleOwner, EventObserver {
            showOneWayPicker()
        })

        viewModel.numWaysSetEvent.observe(viewLifecycleOwner, EventObserver {
            btmDialog.dismiss()
            showDatePicker()
        })

        viewModel.datesSetEvent.observe(viewLifecycleOwner, EventObserver {
            showTimePicker()
        })

        viewModel.timeSetEvent.observe(viewLifecycleOwner, EventObserver {
            getAadhar()
        })
    }

    private fun setDestinations(): Boolean {
        return if (binding.inputStartDestination.editText?.text.toString().isNotBlank()
            && binding.inputEndDestination.editText?.text.toString().isNotBlank()
        ) {
            viewModel.startDestination = binding.inputStartDestination.editText?.text.toString()
            viewModel.endDestination = binding.inputEndDestination.editText?.text.toString()

            true
        } else {
            Toast.makeText(requireActivity(), "Enter the destinations", Toast.LENGTH_SHORT)
                .show()
            false
        }
    }

    private fun showOneWayPicker() {

        val dialogBinding: DialogOneWayPickerBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.dialog_one_way_picker,
                null, false
            )

        dialogBinding.viewModel = viewModel
        dialogBinding.lifecycleOwner = this

        btmDialog = BottomSheetDialog(requireContext())
        btmDialog.setContentView(dialogBinding.root)

        btmDialog.show()
    }

    private fun showDatePicker() {

        if (viewModel.oneWay) {
            val datePicker = datePicker()
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
                        .build()
                )
                .build()

            datePicker.addOnPositiveButtonClickListener {
                viewModel.startDate =
                    SimpleDateFormat("EEEE dd:MM:yyyy", Locale.getDefault()).format(it)
            }
            datePicker.show(parentFragmentManager, "date")
        } else {
            val datePicker = MaterialDatePicker.Builder
                .dateRangePicker()
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
                        .build()
                )
                .build()

            datePicker.addOnPositiveButtonClickListener {
                viewModel.startDate =
                    SimpleDateFormat("EEEE dd:MM:yyyy", Locale.getDefault()).format(it.first)
                viewModel.endDate =
                    SimpleDateFormat("EEEE dd:MM:yyyy", Locale.getDefault()).format(it.second)
            }
            datePicker.show(parentFragmentManager, "date")
        }
    }

    private fun showTimePicker() {

        val timePicker = MaterialTimePicker.Builder()
            .setTitleText("Pickup time")
            .build()
        timePicker.addOnPositiveButtonClickListener {

            viewModel.time = (if (timePicker.hour < 10) "0" else "") +
                    "${timePicker.hour}" + ":" +
                    (if (timePicker.minute < 10) "0" else "") + "${timePicker.minute}"

        }

        timePicker.addOnNegativeButtonClickListener {

            timePicker.dismiss()
        }

        timePicker.show(parentFragmentManager, "time-picker")
    }

    private fun getAadhar() {

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Identity Proof (Aadhar card preferred)")
            .setItems(R.array.identity_options) { _: DialogInterface, i: Int ->
                when (i) {
                    1 -> getImageFromStorage()
                    0 -> getImageFromCamera()

                }
            }

        dialog.show()
    }

    private fun getImageFromStorage() {
        viewModel.getIdentity(IdentitySource.STORAGE)
    }

    private fun getImageFromCamera() {
        viewModel.getIdentity(IdentitySource.CAMERA)
    }


}