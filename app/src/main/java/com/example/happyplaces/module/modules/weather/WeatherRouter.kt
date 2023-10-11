package com.example.happyplaces.module.modules.weather

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class WeatherRouter: WeatherInterface.Router {

    // MARK: - Properties
    lateinit var fragment: WeatherFragment

    // MARK: - Functions
    override fun openLocationPermissionSetting() {
        showDialog("Your location provider is turned off. Please turn it on.")
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        fragment.startForResult.launch(intent)
    }

    override fun showDialog(text: String) {
        Toast.makeText(
            fragment.context,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showRationalDialogForPermissions() {
        fragment.activity?.let {
            AlertDialog.Builder(it)
                .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
                .setPositiveButton(
                    "GO TO SETTINGS"
                ) { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", it.packageName, null)
                        intent.data = uri
                        fragment.startForResult.launch(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("Cancel") { dialog,
                                               _ ->
                    dialog.dismiss()
                }.show()
        }
    }
}