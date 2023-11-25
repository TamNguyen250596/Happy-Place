package com.example.happyplaces.module.modules.happy_place.add_happy_place

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import com.example.happyplaces.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class AddHappyPlaceRouter: AddHappyPlaceInterface.Router {

    // MARK: - Properties
    var activity: AddHappyPlaceActivity? = null

    // MARK: - Functions
    override fun getAddHappyPlaceActivity(): AddHappyPlaceActivity? {
        return activity
    }

    override fun showDatePickerDialog(calendar: java.util.Calendar) {
        activity?.let { activity ->
            DatePickerDialog(
                activity,
                activity,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    override fun showImageGalleryDialog() {
        val items = arrayOf("Select images from gallery", "Capture images from camera")

        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setTitle("Select Action")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> {
                        chooseImageFromGallery()
                    }
                    1 -> {
                        captureImageFromCamera()
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    override fun showToast(title: String) {
        Toast.makeText(activity, title, Toast.LENGTH_SHORT).show()
    }

    override fun openPlacesList() {
    }

    override fun openLocationSetting() {
        activity?.let { activity ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            activity.startActivity(intent)
        }
    }

    private fun chooseImageFromGallery() {
        Dexter.withContext(activity)
            .withPermission(Manifest.permission.READ_MEDIA_IMAGES)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    openGalleryIntent()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    showRequestPermissionDialog(R.string.request_gallery_pick_permission_message)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    showRequestPermissionDialog(R.string.request_gallery_pick_permission_message)
                }
            })
            .onSameThread()
            .check()
    }

    private fun openGalleryIntent() {
        activity?.let { activity ->
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activity.startForResult.launch(intent)
        }
    }

    private fun showRequestPermissionDialog(titleInString: Int) {
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setTitle(titleInString)
        dialogBuilder.setPositiveButton(R.string.go_to_setting) { _, _ ->
            try {
                activity?.let { activity ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    activity.startActivity(intent)
                }
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }.show()
    }

    private fun captureImageFromCamera() {
        Dexter.withContext(activity)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    openCameraIntent()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    showRequestPermissionDialog(R.string.request_camera_permission_message)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    showRequestPermissionDialog(R.string.request_camera_permission_message)
                }
            })
            .onSameThread()
            .check()
    }

    private fun openCameraIntent() {
        activity?.let { activity ->
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activity.startForResult.launch(cameraIntent)
        }
    }
}