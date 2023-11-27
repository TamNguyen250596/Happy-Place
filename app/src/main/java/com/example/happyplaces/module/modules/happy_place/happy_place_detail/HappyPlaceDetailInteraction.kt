package com.example.happyplaces.module.modules.happy_place.happy_place_detail

import android.content.Intent
import android.os.Build
import com.example.happyplaces.module.common.models.HappyPlaceModel
import com.example.happyplaces.module.modules.happy_place.happy_place_list.HappyPlaceListFragment

class HappyPlaceDetailInteraction: HappyPlaceDetailInterface.Interaction {

    // MARK: - Functions
    override fun getHappyPlace(intent: Intent): HappyPlaceModel? {
        return if(intent.hasExtra(HappyPlaceListFragment.putHappyPlace)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(HappyPlaceListFragment.putHappyPlace, HappyPlaceModel::class.java) as HappyPlaceModel
            } else {
                null
            }
        } else {
            null
        }
    }
}