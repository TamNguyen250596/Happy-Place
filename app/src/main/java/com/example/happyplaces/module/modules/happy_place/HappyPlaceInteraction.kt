package com.example.happyplaces.module.modules.happy_place

import android.content.Context
import com.example.happyplaces.module.database.DatabaseHandler
import com.example.happyplaces.module.models.HappyPlaceModel

class HappyPlaceInteraction: HappyPlaceInterface.Interaction {

    override fun getHappyPlacesList(context: Context): ArrayList<HappyPlaceModel> {
        val dbHandler = DatabaseHandler(context)
        return ArrayList(dbHandler.getAllHappyPlaces())
    }
}