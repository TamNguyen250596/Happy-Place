package com.example.happyplaces.module.modules.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.example.happyplaces.R
import com.example.happyplaces.module.common.Constant
import com.example.happyplaces.module.common.WeatherService
import com.example.happyplaces.module.common.models.WeatherResponse
import com.example.happyplaces.module.common.views.CustomDialog
import com.example.happyplaces.module.common.views.WeatherCardType.*
import com.example.happyplaces.module.common.views.WeatherCardViewModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class WeatherInteractionInput: WeatherInterface.InteractionInput {

    // MARK: - Properties
    lateinit var output: WeatherInterface.InteractionOutput
    private lateinit var context: Context
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        context = view.context
        mSharedPreferences = view.context.getSharedPreferences(Constant.PREFERENCE_NAME, Context.MODE_PRIVATE)
        updateView()
    }

    // MARK: - Functions
    override fun getWeatherDetails(
        latitude: Double,
        longitude: Double,
        completion: (Boolean) -> Unit
    ) {
        if (!Constant.isNetworkAvailable(context)) return

        CustomDialog.showSpinner(context)
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: WeatherService = retrofit.create(WeatherService::class.java)
        val listCall: Call<WeatherResponse> = service.getWeather(latitude, longitude, Constant.METRIC_UNIT, Constant.API_KEY)

        listCall.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                CustomDialog.hideSpinner()
                if (response.isSuccessful) {
                    val weatherList = response.body()
                    val weatherResponseJsonString = Gson().toJson(weatherList)
                    val editor = mSharedPreferences.edit()
                    editor.putString(Constant.WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                    editor.apply()
                    updateView()
                }
                completion(response.isSuccessful)
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                CustomDialog.hideSpinner()
                completion(false)
            }
        })
    }

    private fun updateView() {
        val weatherResponseJsonString = mSharedPreferences.getString(Constant.WEATHER_RESPONSE_DATA, "")
        if (weatherResponseJsonString.isNullOrEmpty()) return

        val weatherList = Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)
        weatherList?.let { weatherList ->
            for (z in weatherList.weather.indices) {

                val indicate = weatherList.weather[z]
                output.updateWeatherCardView(WeatherCardViewModel.WeatherCardModel(SNOW, getWeatherIcon(weatherList.weather[z].icon), indicate.main, indicate.description))

                val humidityTitle = weatherList.main.temp.toString() + getUnit(Locale.getDefault().toString())
                val humidityDes = weatherList.main.humidity.toString() + " per cent"
                output.updateWeatherCardView(WeatherCardViewModel.WeatherCardModel(HUMIDITY, null, humidityTitle, humidityDes))

                val temperatureMin = weatherList.main.temp_min.toString() + " min"
                val temperatureMax = weatherList.main.temp_max.toString() + " max"
                output.updateWeatherCardView(WeatherCardViewModel.WeatherCardModel(TEMPERATURE, null, temperatureMin, temperatureMax))

                val windTitle = weatherList.wind.speed.toString()
                val windDes = context.getString(R.string.miles_per_hour)
                output.updateWeatherCardView(WeatherCardViewModel.WeatherCardModel(WIND, null, windTitle, windDes))

                output.updateLocationCardView(weatherList.name, weatherList.sys.country,
                    unixTime(weatherList.sys.sunrise), unixTime(weatherList.sys.sunset))
            }
        }
    }

    private fun getWeatherIcon(iconId: String): Int? {
        return when (iconId) {
            "01d" -> R.drawable.ic_sunny
            "02d", "03d", "04d", "01n", "02n", "03n" -> R.drawable.ic_cloud
            "04n", "10n" -> R.drawable.ic_cloud
            "10d" -> R.drawable.ic_rain
            "11d", "11n" -> R.drawable.ic_storm
            "13d", "13n" -> R.drawable.ic_snowflake
            "50d" -> R.drawable.ic_mist
            else -> null
        }
    }

    private fun getUnit(value: String): String {
        var value = "°C"
        if ("US" == value || "LR" == value || "MM" == value) {
            value = "°F"
        }
        return value
    }

    private fun unixTime(timex: Long): String {
        val date = Date(timex * 1000L)
        @SuppressLint("SimpleDateFormat") val sdf =
            SimpleDateFormat("HH:mm:ss")
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }
}