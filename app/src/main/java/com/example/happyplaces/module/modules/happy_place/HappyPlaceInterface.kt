package com.example.happyplaces.module.modules.happy_place

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.module.models.HappyPlaceModel

interface HappyPlaceInterface {

    interface View {
        fun showHideRecycleView(isGone: Int)
        fun showHidePlaceHolderView(isGone: Int)
    }

    interface Interaction {
        fun getHappyPlacesList(context: Context): ArrayList<HappyPlaceModel>
    }

    interface Presenter {
        fun setupRecycleView(recyclerView: RecyclerView)
    }

    interface Router {
        fun openHappyPlaceDetailActivity(model: HappyPlaceModel)
        fun openAddHappyPlaceActivity(model: HappyPlaceModel)
    }
}