package com.example.happyplaces.module.modules

import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    // MARK: - Properties
    private lateinit var binding: ActivityMapBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap

    // MARK: - Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupView()
        setupAction()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            mMap = googleMap
        }
    }

    // MARK: - Functions
    private fun setupView() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // MARK: - Functions
    private fun setupAction() {
        val textListener = object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = binding.idSearchView.query.toString()
                var addressList: List<Address>? = null

                if (!location.isNullOrEmpty()) {
                    val geocoder = Geocoder(this@MapActivity)
                    try {
                        val geocodeListener = (Geocoder.GeocodeListener { addresses -> addressList = addresses })
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            geocoder.getFromLocationName(location, 1, geocodeListener)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val address = addressList?.get(0)
                    val latLng = LatLng(address?.latitude ?: 0.0, address?.longitude ?: 0.0)
                    mMap.addMarker(MarkerOptions().position(latLng).title(location))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        }
        binding.idSearchView.setOnQueryTextListener(textListener)
    }
}