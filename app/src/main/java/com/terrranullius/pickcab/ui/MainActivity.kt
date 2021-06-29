package com.terrranullius.pickcab.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
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
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.iammert.tileprogressview.TiledProgressView
import com.terrranullius.pickcab.NavGraphDirections
import com.terrranullius.pickcab.R
import com.terrranullius.pickcab.databinding.ActivityMainBinding
import com.terrranullius.pickcab.other.Constants.CAPTURE_IMAGE_IDENTITY
import com.terrranullius.pickcab.other.Constants.SELECT_IMAGE_IDENTITY
import com.terrranullius.pickcab.other.EventObserver
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
        private lateinit var cameraLauncer: ActivityResultLauncher<Void>
        private lateinit var imagePickLauncher: ActivityResultLauncher<String>

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

            setActivityContracts()

            setUpUploadingIdentityProgressBar()
        }

    private fun setActivityContracts() {

        cameraLauncer = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){

            val byteArray = convertIntoByteArray(it)
            val generateName = SimpleDateFormat.getInstance().format(System.currentTimeMillis())

            val firebaseRef = firebaseStorage.reference
            fileRef = firebaseRef.child("proof_images/$generateName")

            uploadIdentityImage(null, byteArray)

        }
        imagePickLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){


        }

    }

    private fun convertIntoByteArray(it: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        it.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        it.recycle()
        return byteArray
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            var fileName = ""

            if (resultCode == Activity.RESULT_OK) {

                btnDialog.show()

                val uri =
                    when (requestCode) {
                        SELECT_IMAGE_IDENTITY -> data?.data
                        CAPTURE_IMAGE_IDENTITY -> viewModel.capturedPhotoUri
                        else -> return
                    }

                fileName = "${uri?.lastPathSegment?.substring(
                    uri.lastPathSegment?.lastIndexOf("/")?.plus(1) ?: 0
                )}"

                val firebaseRef = firebaseStorage.reference
                fileRef = firebaseRef.child("proof_images/$fileName")

                if (requestCode == CAPTURE_IMAGE_IDENTITY) {
                    val byteArray = compressImage(uri!!)
                    uploadIdentityImage(null, byteArray)
                } else if (requestCode == SELECT_IMAGE_IDENTITY) uploadIdentityImage(uri, null)

            }
        }


        private fun uploadIdentityImage(
            uri: Uri? = null,
            byteArray: ByteArray? = null
        ) {
            val uploadTask = uri?.let {
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

        private fun compressImage(imgUri: Uri): ByteArray {
            val bmp = BitmapFactory.decodeFile(imgUri.toString())
            val stream = ByteArrayOutputStream()
            bmp!!.compress(Bitmap.CompressFormat.JPEG, 50, stream)
            val byteArray = stream.toByteArray()
            bmp.recycle()

            return byteArray
        }



}