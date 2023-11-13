package com.example.happyplaces.module.modules.weather

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.happyplaces.R
import com.example.happyplaces.databinding.FragmentWeatherBinding
import com.example.happyplaces.module.common.views.WeatherCard
import com.example.happyplaces.module.common.views.WeatherCardType
import com.example.happyplaces.module.common.views.WeatherCardViewModel

class WeatherFragment : Fragment(), WeatherInterface.View, MenuProvider {

    // MARK: - Properties
    private lateinit var presenter: WeatherInterface.Presenter
    private lateinit var binding: FragmentWeatherBinding
    lateinit var startForResult: ActivityResultLauncher<Intent>
    private val viewModel: WeatherCardViewModel by viewModels()

    // MARK: - Life cycle
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val router =  WeatherRouter()
        val interactionInput = WeatherInteractionInput()
        val presenter = WeatherPresenter()
        presenter.view = this
        presenter.interactionInput = interactionInput
        presenter.router = router
        interactionInput.output = presenter
        router.fragment = this
        this.presenter = presenter
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated(view, savedInstanceState)
        setupView()
        setupAppBar()
        setupStartForResult()
    }

    // MARK: - Functions
    private fun setupStartForResult() {
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            }
    }

    private fun setupView() {
        val transaction = childFragmentManager.beginTransaction()
        val snowCard = WeatherCard.newInstance(WeatherCardType.SNOW, R.drawable.ic_snowflake, "Weather", "condition")
        val humidityCard = WeatherCard.newInstance(WeatherCardType.HUMIDITY, R.drawable.ic_humidity, "Degree", "per cent")
        val temperatureCard = WeatherCard.newInstance(WeatherCardType.TEMPERATURE, R.drawable.ic_temperature, "Minimum", "Maximum")
        val windCard = WeatherCard.newInstance(WeatherCardType.WIND, R.drawable.ic_wind, "Wind", getString(R.string.miles_per_hour))
        transaction.replace(binding.snowCard.id, snowCard)
        transaction.replace(binding.humidityCard.id, humidityCard)
        transaction.replace(binding.temperatureCard.id, temperatureCard)
        transaction.replace(binding.windCard.id, windCard)
        transaction.commit()
    }

    private fun setupAppBar() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // MARK: - WeatherInterface.View
    override fun updateWeatherCardView(value: WeatherCardViewModel.WeatherCardModel) {
        viewModel.updateValue(value)
    }

    override fun updateLocationCardView(
        title: String,
        description: String,
        sunRiseTime: String,
        sunSetTime: String
    ) {
        binding.tvName.text = title
        binding.tvCountry.text = description
        binding.tvSunriseTime.text = sunRiseTime
        binding.tvSunsetTime.text = sunSetTime
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_refresh -> {
                presenter.selectedRefreshButton()
                return true
            }
            else -> false
        }
    }
}