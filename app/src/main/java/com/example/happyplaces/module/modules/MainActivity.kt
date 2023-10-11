package com.example.happyplaces.module.modules

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.module.modules.happy_place.HappyPlaceFragment
import com.example.happyplaces.module.modules.weather.WeatherFragment

class MainActivity : AppCompatActivity() {

    // MARK: - Properties
    private lateinit var binding: ActivityMainBinding
    private var currentFragmentTag = ""

    // MARK: - Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupView()
        setupAction()
    }

    // MARK: Functions
    private fun setupView() {
        loadFragment(HappyPlaceFragment(), R.id.happy_place.toString())
    }

    private fun loadFragment(fragment: Fragment, tag: String){
        if (tag == currentFragmentTag) return
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.container.id, fragment, tag)
        transaction.commit()
        currentFragmentTag = tag
    }

    // MARK: - Actions
    private fun setupAction() {
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.happy_place -> {
                    loadFragment(HappyPlaceFragment(), R.id.happy_place.toString())
                    true
                }
                R.id.weather -> {
                    loadFragment(WeatherFragment(), R.id.weather.toString())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}