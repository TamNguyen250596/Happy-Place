package com.example.happyplaces.module.modules.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import com.example.happyplaces.module.common.views.WeatherCardViewModel
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class WeatherPresenter: WeatherInterface.Presenter, WeatherInterface.InteractionOutput {

    // MARK: - Properties
    lateinit var view: WeatherInterface.View
    lateinit var interactionInput: WeatherInterface.InteractionInput
    lateinit var router: WeatherInterface.Router
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    // MARK: - Life cycle
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        interactionInput.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

        if (!isLocationEnabled(view.context)) {
            router.openLocationPermissionSetting()
        } else {
            Dexter.withContext(view.context)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            requestLocationData()
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            router.showDialog("You have denied location permission. Please allow it is mandatory.")
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        router.showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
        }
    }

    override fun selectedRefreshButton() {
        requestLocationData()
    }

    // MARK: - Functions
    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = getSystemService(context, LocationManager::class.java) ?: return false
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {

        val mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(100)
            .build()

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            val latitude = mLastLocation?.latitude ?: 0.0
            val longitude = mLastLocation?.longitude ?: 0.0
            interactionInput.getWeatherDetails(latitude, longitude) { success ->
                if (success) mFusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    // MARK: - InteractionOutput
    override fun updateWeatherCardView(value: WeatherCardViewModel.WeatherCardModel) {
        view.updateWeatherCardView(value)
    }

    override fun updateLocationCardView(
        title: String,
        description: String,
        sunRiseTime: String,
        sunSetTime: String
    ) {
        view.updateLocationCardView(title, description, sunRiseTime, sunSetTime)
    }
}