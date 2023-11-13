package com.example.happyplaces.module.common.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.happyplaces.databinding.FragmentWeatherCardBinding

private const val tagKey = "tag"
private const val imageIdKey = "imageId"
private const val titleKey = "title"
private const val descriptionKey = "description"

class WeatherCard : Fragment() {

    // MARK: - Properties
    private var tag: String? = null
    private var imageId: Int? = null
    private var title: String? = null
    private var description: String? = null
    private lateinit var binding: FragmentWeatherCardBinding
    private val viewModel: WeatherCardViewModel by viewModels({requireParentFragment()})

    // MARK: - Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tag = it.getString(tagKey)
            imageId = it.getInt(imageIdKey)
            title = it.getString(titleKey)
            description = it.getString(descriptionKey)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeatherCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
    }

    // MARK: - Functions
    private fun setupView() {
        imageId?.let {
            binding.ivMain.setImageResource(it)
        }
        binding.tvMain.text = title
        binding.tvMainDescription.text = description
    }

    private fun observeViewModel() {
        viewModel.weatherCardValue.observe(viewLifecycleOwner) { weatherCardModel ->
            if (weatherCardModel.tag.toString() == tag) {
                weatherCardModel.imageId?.let {
                    binding.ivMain.setImageResource(it)
                }
                binding.tvMain.text = weatherCardModel.title
                binding.tvMainDescription.text = weatherCardModel.description
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(tag: WeatherCardType, imageId: Int, title: String, description: String) =
            WeatherCard().apply {
                arguments = Bundle().apply {
                    putString(tagKey, tag.toString())
                    putInt(imageIdKey, imageId)
                    putString(titleKey, title)
                    putString(descriptionKey, description)
                }
            }
    }
}

class WeatherCardViewModel: ViewModel() {

    data class WeatherCardModel(val tag: WeatherCardType, val imageId: Int?, val title: String, val description: String)
    val weatherCardValue = MutableLiveData<WeatherCardModel>()

    fun updateValue(value: WeatherCardModel) {
        weatherCardValue.value = value
    }
}

enum class WeatherCardType {
    SNOW, HUMIDITY, TEMPERATURE, WIND
}