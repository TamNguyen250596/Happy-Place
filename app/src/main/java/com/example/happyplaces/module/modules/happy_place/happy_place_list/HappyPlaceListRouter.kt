package com.example.happyplaces.module.modules.happy_place.happy_place_list

import android.content.Intent
import com.example.happyplaces.module.common.models.HappyPlaceModel
import com.example.happyplaces.module.modules.happy_place.add_happy_place.AddHappyPlaceActivity
import com.example.happyplaces.module.modules.HappyPlaceDetailActivity

class HappyPlaceListRouter: HappyPlaceListInterface.Router {

    // MARK: - Properties
    lateinit var fragment: HappyPlaceListFragment

    override fun openHappyPlaceDetailActivity(model: HappyPlaceModel) {
        val intent = Intent(fragment.activity, HappyPlaceDetailActivity::class.java)
        intent.putExtra(HappyPlaceListFragment.putHappyPlace, model)
        fragment.startForResult.launch(intent)
    }

    override fun openAddHappyPlaceActivity(model: HappyPlaceModel) {
        val intent = Intent(fragment.activity, AddHappyPlaceActivity::class.java)
        intent.putExtra(HappyPlaceListFragment.putHappyPlace, model)
        fragment.startForResult.launch(intent)
    }


}