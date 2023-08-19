package com.example.happyplaces.module

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import java.util.*
import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import com.example.happyplaces.R
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AddHappyPlaceActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    // MARK: - Properties
    private lateinit var binding: ActivityAddHappyPlaceBinding
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    // MARK: - Companion
    companion object {
        const val IMAGE_DIRECTORY = "happy_places_images"
    }

    // MARK: - Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupStartForResult()
        setupView()
        implementActions()
    }

    // MARK: - Functions
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        calendar.set(p1, p2, p3)
        binding.etDate.setText(dateFormatter.format(calendar.timeInMillis).toString())
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupStartForResult() {
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data?.data != null) {
                    binding.ivPlaceImage.setImageURI(it.data?.data)
                }
                if (it.data?.extras != null) {
                    val bitmap: Bitmap? = it.data?.extras?.getParcelable("data", Bitmap::class.java)
                    binding.ivPlaceImage.setImageBitmap(bitmap)
                }
                saveImageToInternalStorage(binding.ivPlaceImage.drawable.toBitmap())
            }
        }
    }

    private fun showDatePickerDialog() {
        DatePickerDialog(
            this,
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showImageGalleryDialog() {
        val items = arrayOf("Select images from gallery", "Capture images from camera")

        val dialogBuilder = AlertDialog.Builder(this)
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

    // MARK: - Actions
    private fun implementActions() {
        binding.etDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.tvAddImage.setOnClickListener {
            showImageGalleryDialog()
        }
    }

    // MARK: - Handle Images
    private fun chooseImageFromGallery() {
        Dexter.withContext(this)
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
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startForResult.launch(intent)
    }

    private fun showRequestPermissionDialog(titleInString: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(titleInString)
        dialogBuilder.setPositiveButton(R.string.go_to_setting) { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }.show()
    }

    private fun captureImageFromCamera() {
        Dexter.withContext(this)
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
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startForResult.launch(cameraIntent)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wraper = ContextWrapper(applicationContext)
        var file = wraper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }
}