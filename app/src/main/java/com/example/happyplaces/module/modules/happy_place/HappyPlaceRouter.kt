package com.example.happyplaces.module.modules.happy_place

import android.content.Intent
import com.example.happyplaces.module.common.models.HappyPlaceModel
import com.example.happyplaces.module.modules.AddHappyPlaceActivity
import com.example.happyplaces.module.modules.HappyPlaceDetailActivity

class HappyPlaceRouter(
    private val fragment: HappyPlaceFragment
): HappyPlaceInterface.Router {

    override fun openHappyPlaceDetailActivity(model: HappyPlaceModel) {
        val intent = Intent(fragment.activity, HappyPlaceDetailActivity::class.java)
        intent.putExtra(HappyPlaceFragment.putHappyPlace, model)
        fragment.startForResult.launch(intent)
    }

    override fun openAddHappyPlaceActivity(model: HappyPlaceModel) {
        val intent = Intent(fragment.activity, AddHappyPlaceActivity::class.java)
        intent.putExtra(HappyPlaceFragment.putHappyPlace, model)
        fragment.startForResult.launch(intent)
    }


}