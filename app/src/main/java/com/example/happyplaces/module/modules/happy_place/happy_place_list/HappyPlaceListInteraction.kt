package com.example.happyplaces.module.modules.happy_place.happy_place_list

import android.content.Context
import com.example.happyplaces.module.common.database.DatabaseHandler
import com.example.happyplaces.module.common.models.HappyPlaceModel

class HappyPlaceListInteraction: HappyPlaceListInterface.Interaction {

    override fun getHappyPlacesList(context: Context): ArrayList<HappyPlaceModel> {
        val dbHandler = DatabaseHandler(context)
        return ArrayList(dbHandler.getAllHappyPlaces())
    }
}