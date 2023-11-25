package com.example.happyplaces.module.modules.happy_place.add_happy_place

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import com.example.happyplaces.module.common.models.HappyPlaceModel
import com.example.happyplaces.module.modules.happy_place.happy_place_list.HappyPlaceListFragment
import java.io.InputStream

class AddHappyPlaceInteraction: AddHappyPlaceInterface.Interaction {

    // MARK: - Functions
    override fun getHappyPlaceModel(activity: AddHappyPlaceActivity): HappyPlaceModel? {
        val intent = activity.intent

        if (intent.hasExtra(HappyPlaceListFragment.putHappyPlace)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return intent.getParcelableExtra(HappyPlaceListFragment.putHappyPlace, HappyPlaceModel::class.java) as HappyPlaceModel
            }
        }
        return null
    }

    override fun uriToBitmap(activity: AddHappyPlaceActivity, uri: Uri): Bitmap? {
        var inputStream: InputStream? = null
        try {
            inputStream = activity.applicationContext.contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return null
    }
}