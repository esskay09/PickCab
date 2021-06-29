package com.terrranullius.pickcab.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
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
import com.terrranullius.pickcab.other.Constants.CAPTURE_IMAGE_IDENTITY
import com.terrranullius.pickcab.other.Constants.SELECT_IMAGE_IDENTITY
import com.terrranullius.pickcab.other.EventObserver
import com.terrranullius.pickcab.ui.viewmodels.MainViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private lateinit var btmDialog: BottomSheetDialog
    private lateinit var binding: FragmentMainBinding
    private lateinit var currentPhotoPath: String
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
                    1 -> {
                        val imageIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        requireActivity().startActivityForResult(imageIntent, SELECT_IMAGE_IDENTITY)
                    }
                    0 -> startCameraIntent()

                }
            }

        dialog.show()
    }

    private fun startCameraIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.terrranullius.pickcab",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                    viewModel.capturedPhotoUri = Uri.parse(currentPhotoPath)

                    requireActivity().startActivityForResult(
                        takePictureIntent,
                        CAPTURE_IMAGE_IDENTITY
                    )
                }
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

}