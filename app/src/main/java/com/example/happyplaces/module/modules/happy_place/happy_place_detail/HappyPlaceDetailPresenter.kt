package com.example.happyplaces.module.modules.happy_place.happy_place_detail

import android.os.Bundle
import com.example.happyplaces.module.common.models.HappyPlaceModel


class HappyPlaceDetailPresenter: HappyPlaceDetailInterface.Presenter {

    // MARK: - Properties
    lateinit var view: HappyPlaceDetailInterface.View
    lateinit var interaction: HappyPlaceDetailInterface.Interaction
    lateinit var router: HappyPlaceDetailInterface.Router
    private  var happyPlace: HappyPlaceModel? = null

    // MARK: - Functions
    override fun onCreate(savedInstanceState: Bundle?) {
        val activity = router.getHappyPlaceDetailActivity() ?: return
        happyPlace = interaction.getHappyPlace(activity.intent)
        happyPlace?.let { happyPlaceModel ->
            view.updateViewContent(happyPlaceModel)
        }
    }

    override fun selectedViewOnMapButton() {
        router.openMapActivity()
    }

}