package com.example.happyplaces.module.modules.happy_place.happy_place_detail

import android.content.Intent
import android.os.Bundle
import com.example.happyplaces.module.common.models.HappyPlaceModel

interface HappyPlaceDetailInterface {

    interface View {
        fun updateViewContent(happyPlaceModel: HappyPlaceModel)
    }

    interface Interaction {
        fun getHappyPlace(intent: Intent): HappyPlaceModel?
    }

    interface Presenter {
        fun onCreate(savedInstanceState: Bundle?)
        fun selectedViewOnMapButton()
    }

    interface Router {
        fun getHappyPlaceDetailActivity(): HappyPlaceDetailActivity?
        fun openMapActivity()
    }
}