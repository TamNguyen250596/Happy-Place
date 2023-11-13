package com.example.happyplaces.module.modules

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.example.happyplaces.databinding.ActivityHappPlaceDetailBinding
import com.example.happyplaces.module.common.models.HappyPlaceModel
import com.example.happyplaces.module.modules.happy_place.HappyPlaceFragment

class HappyPlaceDetailActivity : AppCompatActivity() {

    // MARK: - Properties
    private lateinit var binding: ActivityHappPlaceDetailBinding
    private lateinit var happyPlace: HappyPlaceModel

    // MARK: - Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappPlaceDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getData()
        setupView()
        implementAction()
    }

    // MARK: - Get Data
    private fun getData() {
        if(intent.hasExtra(HappyPlaceFragment.putHappyPlace)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                happyPlace = intent.getParcelableExtra(HappyPlaceFragment.putHappyPlace, HappyPlaceModel::class.java) as HappyPlaceModel
            }
        }
    }

    private fun implementAction() {
        binding.btnViewOnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }

    // MARK: - Get Data
    private fun setupView() {
        setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = happyPlace.title
        binding.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.ivPlaceImage.setImageURI(happyPlace.image.toUri())
        binding.tvDescription.text = happyPlace.description
        binding.tvLocation.text = happyPlace.location
    }
}