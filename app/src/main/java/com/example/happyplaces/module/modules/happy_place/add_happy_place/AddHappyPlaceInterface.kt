package com.example.happyplaces.module.modules.happy_place.add_happy_place

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.result.ActivityResult
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.module.common.models.HappyPlaceModel

interface AddHappyPlaceInterface {

    interface View {
        fun updateImageView(bitmap: Bitmap?)
        fun updateViewContent(happyPlace: HappyPlaceModel)
    }

    interface Interaction {
        fun getHappyPlaceModel(activity: AddHappyPlaceActivity): HappyPlaceModel?
        fun uriToBitmap(activity: AddHappyPlaceActivity, uri: Uri): Bitmap?
    }

    interface Presenter {
        fun onCreate(savedInstanceState: Bundle?)
        fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int)
        fun handleActivityResult(result: ActivityResult)
        fun selectedDateTextView()
        fun selectedAddImageTextView()
        fun selectedSaveButton(binding: ActivityAddHappyPlaceBinding)
        fun selectedLocationTextView()
        fun selectedCurrentLocationTextView()
    }

    interface Router {
        fun getAddHappyPlaceActivity(): AddHappyPlaceActivity?
        fun showDatePickerDialog(calendar: java.util.Calendar)
        fun showImageGalleryDialog()
        fun showToast(title: String)
        fun openPlacesList()
        fun openLocationSetting()
    }
}