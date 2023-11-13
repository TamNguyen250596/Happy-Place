package com.example.happyplaces.module.modules.weather

import android.content.Context
import android.os.Bundle
import com.example.happyplaces.module.common.views.WeatherCardViewModel

interface WeatherInterface {

    interface View {
        fun updateWeatherCardView(value: WeatherCardViewModel.WeatherCardModel)
        fun updateLocationCardView(title: String, description: String, sunRiseTime: String, senSetTime: String)
    }

    interface InteractionInput {
        fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?)
        fun getWeatherDetails(latitude: Double, longitude: Double, completion: (Boolean) -> Unit)
    }

    interface InteractionOutput {
        fun updateWeatherCardView(value: WeatherCardViewModel.WeatherCardModel)
        fun updateLocationCardView(title: String, description: String, sunRiseTime: String, senSetTime: String)
    }

    interface Presenter {
        fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?)
        fun selectedRefreshButton()
    }

    interface Router {
        fun openLocationPermissionSetting()
        fun showDialog(text: String)
        fun showRationalDialogForPermissions()
    }
}