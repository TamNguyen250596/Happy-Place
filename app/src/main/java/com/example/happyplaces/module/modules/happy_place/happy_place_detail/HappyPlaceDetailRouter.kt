package com.example.happyplaces.module.modules.happy_place.happy_place_detail

import android.content.Intent
import com.example.happyplaces.module.modules.MapActivity

class HappyPlaceDetailRouter: HappyPlaceDetailInterface.Router {

    // MARK: - Properties
    var activity: HappyPlaceDetailActivity? = null

    // MARK: - Functions
    override fun getHappyPlaceDetailActivity(): HappyPlaceDetailActivity? {
        return activity
    }

    override fun openMapActivity() {
        val activity = activity ?: return
        val intent = Intent(activity, MapActivity::class.java)
        activity.startActivity(intent)
    }

}