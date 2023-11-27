package com.example.happyplaces.module.modules.happy_place.add_happy_place

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import java.util.*
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.example.happyplaces.R
import com.example.happyplaces.module.common.models.HappyPlaceModel

class AddHappyPlaceActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, AddHappyPlaceInterface.View {

    // MARK: - Properties
    private lateinit var binding: ActivityAddHappyPlaceBinding
    private lateinit var presenter: AddHappyPlacePresenter
    lateinit var startForResult: ActivityResultLauncher<Intent>
    private val calendar = Calendar.getInstance()
    @SuppressLint("NewApi")
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    // MARK: - Companion
    companion object {
        const val IMAGE_DIRECTORY = "happy_places_images"
        const val PLACE_AUTO_COMPLETE_REQUEST_CODE = 3
    }

    // MARK: - Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val router = AddHappyPlaceRouter()
        val interaction = AddHappyPlaceInteraction()
        val presenter = AddHappyPlacePresenter()
        router.activity = this
        presenter.view = this
        presenter.router = router
        presenter.interaction = interaction
        this.presenter = presenter
        presenter.onCreate(savedInstanceState)
        setupView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setupStartForResult()
        }
        implementActions()
    }

    // MARK: - Functions
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        presenter.onDateSet(p0, p1, p2, p3)
        binding.etDate.setText(dateFormatter.format(calendar.timeInMillis).toString())
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // MARK: - Actions
    private fun implementActions() {
        binding.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.etDate.setOnClickListener {
            presenter.selectedDateTextView()
        }

        binding.tvAddImage.setOnClickListener {
            presenter.selectedAddImageTextView()
        }

        binding.btnSave.setOnClickListener {
            presenter.selectedSaveButton(binding)
        }

        binding.etLocation.setOnClickListener {
            presenter.selectedLocationTextView()
        }

        binding.tvSelectCurrentLocation.setOnClickListener {
            presenter.selectedCurrentLocationTextView()
        }
    }

    private fun setupStartForResult() {
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            presenter.handleActivityResult(result)
        }

    }

    // MARK: - Presenter
    override fun updateImageView(bitmap: Bitmap?) {
        binding.ivPlaceImage.setImageBitmap(bitmap)
    }

    override fun updateViewContent(happyPlace: HappyPlaceModel) {
        supportActionBar?.title = happyPlace.title
        binding.etTitle.setText(happyPlace.title)
        binding.etDescription.setText(happyPlace.description)
        binding.etLocation.setText(happyPlace.location)
        binding.etDate.setText(happyPlace.date)
        binding.ivPlaceImage.setImageURI(happyPlace.image.toUri())
        binding.btnSave.text = getString(R.string.update)
    }
}