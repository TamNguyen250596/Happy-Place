package com.example.happyplaces.module.modules.happy_place.happy_place_detail

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.example.happyplaces.databinding.ActivityHappPlaceDetailBinding
import com.example.happyplaces.module.common.models.HappyPlaceModel
import com.example.happyplaces.module.modules.MapActivity

class HappyPlaceDetailActivity : AppCompatActivity(), HappyPlaceDetailInterface.View {

    // MARK: - Properties
    private lateinit var binding: ActivityHappPlaceDetailBinding
    private lateinit var presenter: HappyPlaceDetailPresenter

    // MARK: - Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappPlaceDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val router = HappyPlaceDetailRouter()
        val interaction = HappyPlaceDetailInteraction()
        val presenter = HappyPlaceDetailPresenter()
        router.activity = this
        presenter.view = this
        presenter.router = router
        presenter.interaction = interaction
        this.presenter = presenter
        setupView()
        implementAction()
        presenter.onCreate(savedInstanceState)

    }

    private fun implementAction() {
        binding.btnViewOnMap.setOnClickListener {
            presenter.selectedViewOnMapButton()
        }

        binding.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // MARK: - Functions
    private fun setupView() {
        setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // MARK: - View
    override fun updateViewContent(happyPlaceModel: HappyPlaceModel) {
        supportActionBar?.title = happyPlaceModel.title
        binding.ivPlaceImage.setImageURI(happyPlaceModel.image.toUri())
        binding.tvDescription.text = happyPlaceModel.description
        binding.tvLocation.text = happyPlaceModel.location
    }
}