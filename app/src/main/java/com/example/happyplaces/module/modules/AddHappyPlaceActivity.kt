package com.example.happyplaces.module.modules

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
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.example.happyplaces.BuildConfig
import com.example.happyplaces.R
import com.example.happyplaces.module.database.DatabaseHandler
import com.example.happyplaces.module.models.HappyPlaceModel
import com.example.happyplaces.module.modules.happy_place.HappyPlaceFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AddHappyPlaceActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    // MARK: - Properties
    private lateinit var binding: ActivityAddHappyPlaceBinding
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var placesClient: PlacesClient
    private var happyPlace: HappyPlaceModel? = null
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var savedImageUri: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    // MARK: - Companion
    companion object {
        const val IMAGE_DIRECTORY = "happy_places_images"
        const val PLACE_AUTO_COMPLETE_REQUEST_CODE = 3
    }

    // MARK: - Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getData()
        setupStartForResult()
        setupView()
        implementActions()
        setupPlaces()
    }

    // MARK: - Get data
    private fun getData() {
        if(intent.hasExtra(HappyPlaceFragment.putHappyPlace)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                happyPlace = intent.getParcelableExtra(HappyPlaceFragment.putHappyPlace, HappyPlaceModel::class.java) as HappyPlaceModel
            }
        }
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

        happyPlace?.let { place ->
            supportActionBar?.title = place.title
            binding.etTitle.setText(place.title)
            binding.etDescription.setText(place.description)
            binding.etLocation.setText(place.location)
            binding.etDate.setText(place.date)
            savedImageUri = place.image.toUri()
            binding.ivPlaceImage.setImageURI(savedImageUri)
            binding.btnSave.text = getString(R.string.update)
        }
    }

    private fun setupStartForResult() {
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                if (data?.data != null) binding.ivPlaceImage.setImageURI(data.data)
                if (data?.extras != null) {
//                    val bitmap: Bitmap? = data.extras?.getParcelable("data", Bitmap::class.java)
//                    binding.ivPlaceImage.setImageBitmap(bitmap)
                }
                savedImageUri = saveImageToInternalStorage(binding.ivPlaceImage.drawable.toBitmap())
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

        binding.btnSave.setOnClickListener {
            if(happyPlace != null) {
                editHappyPlaceModel()
            } else {
                saveHappyPlaceModel()
            }
        }

        binding.etLocation.setOnClickListener {
            openPlacesList()
        }

        binding.tvSelectCurrentLocation.setOnClickListener {
            selectedGetCurrentLocationTV()
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

    private fun saveHappyPlaceModel() {
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()
        val location = binding.etLocation.text.toString()
        val date = binding.etDate.text.toString()

        when {
            title.isNullOrEmpty() -> {
                showToast(getString(R.string.please_enter_title))
            }
            description.isNullOrEmpty() -> {
                showToast(getString(R.string.please_enter_description))
            }
//            location.isNullOrEmpty() -> {
//                showToast(getString(R.string.please_select_location))
//            }
            savedImageUri == null -> {
                showToast(getString(R.string.please_add_image))
            } else -> {
            val happyPlaceModel = HappyPlaceModel(
                0,
                title,
                savedImageUri.toString(),
                description,
                date,
                location,
                mLatitude,
                mLongitude
            )
            val dbHandler = DatabaseHandler(this)
            val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
            if (addHappyPlace > 0) {
                setResult(HappyPlaceFragment.didSaveHappyPlace)
                finish()
            }
            }
        }
    }

    private fun editHappyPlaceModel() {
        happyPlace?.title = binding.etTitle.text.toString()
        happyPlace?.description = binding.etDescription.text.toString()
        happyPlace?.location = binding.etLocation.text.toString()
        happyPlace?.date = binding.etDate.text.toString()
        happyPlace?.image = savedImageUri.toString()
        val dbHandler = DatabaseHandler(this)
        happyPlace?.let {
           val editHappyPlace = dbHandler.updateHappyPlace(it)
            if (editHappyPlace > 0) {
                setResult(HappyPlaceFragment.didSaveHappyPlace)
                finish()
            }
        }
    }

    private fun showToast(title: String) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
    }

    // MARK: - Map
    private fun setupPlaces() {
        if(!Places.isInitialized()) {
            Places.initialize(this@AddHappyPlaceActivity, BuildConfig.GOOGLE_MAPS_API_KEY)
            placesClient = Places.createClient(this@AddHappyPlaceActivity)
        }
    }

    private fun openPlacesList() {
        try {
            val intent = Intent(this@AddHappyPlaceActivity, MapActivity::class.java)
            startForResult.launch(intent)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectedGetCurrentLocationTV() {
        if (!isLocationEnabled()) {
            Toast.makeText(
                this,
                "Your location provider is turned off. Please turn it on.",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {

                            Toast.makeText(
                                this@AddHappyPlaceActivity,
                                "Location permission is granted. Now you can request for a current location.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showToast("Location Perrmision error")
                    }
                }).onSameThread()
                .check()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}