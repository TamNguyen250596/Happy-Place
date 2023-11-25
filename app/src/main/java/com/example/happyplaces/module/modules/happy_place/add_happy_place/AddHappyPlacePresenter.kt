package com.example.happyplaces.module.modules.happy_place.add_happy_place

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.result.ActivityResult
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.module.common.database.DatabaseHandler
import com.example.happyplaces.module.common.models.HappyPlaceModel
import com.example.happyplaces.module.modules.happy_place.happy_place_list.HappyPlaceListFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class AddHappyPlacePresenter: AddHappyPlaceInterface.Presenter {

    // MARK: Properties
    lateinit var view: AddHappyPlaceInterface.View
    lateinit var interaction: AddHappyPlaceInterface.Interaction
    lateinit var router: AddHappyPlaceInterface.Router
    private var happyPlace: HappyPlaceModel? = null
    private var savedImageUri: Uri? = null
    private val calendar = Calendar.getInstance()
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    // MARK: Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        val activity = router.getAddHappyPlaceActivity() ?: return

        interaction.getHappyPlaceModel(activity)?.let { happyPlaceModel ->
            this.happyPlace = happyPlaceModel
            view.updateViewContent(happyPlaceModel)
        }
    }

    // MARK: Functions
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        calendar.set(p1, p2, p3)
    }

    override fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            var bitmap: Bitmap? = null
            data?.data?.let { uri ->
                val activity = router.getAddHappyPlaceActivity() ?: return
                bitmap = interaction.uriToBitmap(activity, uri)
            }
            data?.extras?.let { extras ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bitmap = extras.getParcelable("data", Bitmap::class.java)
                }
            }
            bitmap?.let { bitmap ->
                view.updateImageView(bitmap)
                savedImageUri = saveImageToInternalStorage(bitmap)

            }
        }
    }

    override fun selectedDateTextView() {
        router.showDatePickerDialog(calendar)
    }

    override fun selectedAddImageTextView() {
        router.showImageGalleryDialog()
    }

    override fun selectedSaveButton(binding: ActivityAddHappyPlaceBinding) {
        if(happyPlace != null) {
            editHappyPlaceModel(binding)
        } else {
            saveHappyPlaceModel(binding)
        }
    }

    override fun selectedLocationTextView() {
        router.openPlacesList()
    }

    override fun selectedCurrentLocationTextView() {
        val activity = router.getAddHappyPlaceActivity() ?: return

        if (!isLocationEnabled()) {
            router.showToast("Your location provider is turned off. Please turn it on.")
            router.openLocationSetting()
        } else {
            Dexter.withContext(activity)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            router.showToast("Location permission is granted. Now you can request for a current location.")
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        router.showToast("Location Permission error")
                    }
                }).onSameThread()
                .check()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val activity = router.getAddHappyPlaceActivity() ?: return false

        val locationManager: LocationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun saveHappyPlaceModel(binding: ActivityAddHappyPlaceBinding) {
        val activity = router.getAddHappyPlaceActivity() ?: return

        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()
        val location = binding.etLocation.text.toString()
        val date = binding.etDate.text.toString()

        when {
            title.isNullOrEmpty() -> {
                router.showToast(activity.getString(R.string.please_enter_title))
            }
            description.isNullOrEmpty() -> {
                router.showToast(activity.getString(R.string.please_enter_description))
            }
            location.isNullOrEmpty() -> {
                router.showToast(activity.getString(R.string.please_select_location))
            }
            savedImageUri == null -> {
                router.showToast(activity.getString(R.string.please_add_image))
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
            val dbHandler = DatabaseHandler(activity)
            val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
            if (addHappyPlace > 0) {
                activity.setResult(HappyPlaceListFragment.didSaveHappyPlace)
                activity.finish()
            }
        }
        }
    }

    private fun editHappyPlaceModel(binding: ActivityAddHappyPlaceBinding) {
        val activity = router.getAddHappyPlaceActivity() ?: return

        happyPlace?.title = binding.etTitle.text.toString()
        happyPlace?.description = binding.etDescription.text.toString()
        happyPlace?.location = binding.etLocation.text.toString()
        happyPlace?.date = binding.etDate.text.toString()
        happyPlace?.image = savedImageUri.toString()
        val dbHandler = DatabaseHandler(activity)
        happyPlace?.let {
            val editHappyPlace = dbHandler.updateHappyPlace(it)
            if (editHappyPlace > 0) {
                activity.setResult(HappyPlaceListFragment.didSaveHappyPlace)
                activity.finish()
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
        val activity = router.getAddHappyPlaceActivity() ?: return null

        val wraper = ContextWrapper(activity.applicationContext)
        var file = wraper.getDir(AddHappyPlaceActivity.IMAGE_DIRECTORY, Context.MODE_PRIVATE)
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