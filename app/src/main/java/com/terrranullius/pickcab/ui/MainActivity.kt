package com.terrranullius.pickcab.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.iammert.tileprogressview.TiledProgressView
import com.terrranullius.pickcab.NavGraphDirections
import com.terrranullius.pickcab.R
import com.terrranullius.pickcab.databinding.ActivityMainBinding
import com.terrranullius.pickcab.other.EventObserver
import com.terrranullius.pickcab.other.IdentitySource.CAMERA
import com.terrranullius.pickcab.other.IdentitySource.STORAGE
import com.terrranullius.pickcab.ui.viewmodels.MainViewModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val firebaseStorage = FirebaseStorage.getInstance()
    private val viewModel: MainViewModel by viewModels()
    private lateinit var fileRef: StorageReference

    private lateinit var btnDialog: BottomSheetDialog
    private lateinit var btmDialogLayout: View
    private lateinit var cameraLaunhcer: ActivityResultLauncher<Void>
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        viewModel.identitySetEvent.observe(this, EventObserver {
            val action = NavGraphDirections.actionGlobalBookingFinished()
            (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment)
                .findNavController()
                .navigate(action)
        })

        registerActivityContracts()

        setUpUploadingIdentityProgressBar()

        setObservers()
    }

    private fun setObservers(){
        viewModel.getIdentityEvent.observe(this, EventObserver{
            when(it){
                CAMERA -> cameraLaunhcer.launch(null)
                STORAGE -> imagePickerLauncher.launch("image/*")
            }
        })
    }

    private fun registerActivityContracts() {

        cameraLaunhcer = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {

            btnDialog.show()

            val byteArray = convertBitmapIntoByteArray(it)

            try {
                uploadIdentityImage(null, byteArray)
            } catch (e: Exception){
                Log.d("sha", "Error Uploading Image ${e.message}")
            }

        }
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {

            btnDialog.show()

            var bytes : ByteArray? = null

            try {
                contentResolver.openInputStream(it)?.readBytes()?.let { byteArray -> bytes = compressImage(byteArray) } ?:  Log.d("Pickcab", "Error Compressing Image byteArrau NUll")
            } catch (e: Exception){
                Log.d("sha", "Error Compressing Image ${e.message}")
            }
            try {
                bytes?.let {byteArray ->
                    uploadIdentityImage(null, byteArray)
                }?: uploadIdentityImage(it, null)
            } catch (e: Exception){
                Log.d("sha", "Error Uploading Image ${e.message}")
            }

        }

    }

    private fun convertBitmapIntoByteArray(it: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        it.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        it.recycle()
        return byteArray
    }


    private fun uploadIdentityImage(
        uri: Uri? = null,
        byteArray: ByteArray? = null
    ) {

        val generateName = SimpleDateFormat.getInstance().format(System.currentTimeMillis()).substringAfterLast("/")
        val firebaseRef = firebaseStorage.reference
        fileRef = firebaseRef.child("proof_images/$generateName")

        val uploadTask: UploadTask? = uri?.let {
            fileRef.putFile(it)
        } ?: byteArray?.let { fileRef.putBytes(it) }

        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            fileRef.downloadUrl
        }?.addOnCompleteListener { task ->

            btnDialog.dismiss()

            if (task.isSuccessful) {
                val downloadUri = task.result
                viewModel.identityProofFireUri = downloadUri.toString()
            }
        }?.addOnFailureListener {

        }

        uploadTask?.addOnProgressListener { (bytesTransferred, totalByteCount) ->
            val progress = (100.0 * bytesTransferred) / totalByteCount
            btmDialogLayout.findViewById<TiledProgressView>(R.id.pv_identity_upload)
                .setProgress(progress.toFloat())
        }
    }

    private fun setUpUploadingIdentityProgressBar() {
        btnDialog = BottomSheetDialog(this)
        btmDialogLayout =
            LayoutInflater.from(this).inflate(R.layout.dialog_progress_identity_upload, null)
                .apply {
                    findViewById<TiledProgressView>(R.id.pv_identity_upload)
                        .apply {
                            setColorRes(R.color.white)
                            setLoadingColorRes(R.color.blue_primary_dark)
                        }
                }

        btnDialog.setContentView(btmDialogLayout)
    }

    private fun compressImage(inputArray: ByteArray): ByteArray {
        val bmp = BitmapFactory.decodeByteArray(inputArray,0 , inputArray.size)

        val quality = if(bmp.byteCount > 1024 * 1024 * 20){
            ((1024 * 1024 * 20)/bmp.byteCount.toFloat()) * 100f
        } else 100

        Log.d("sha", quality.toString())

        val stream = ByteArrayOutputStream()
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 60, stream)
        bmp.recycle()

        return stream.toByteArray()

    }


}