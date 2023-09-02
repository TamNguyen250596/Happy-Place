package com.example.happyplaces.module.modules

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.module.modules.happy_place.HappyPlaceFragment

class MainActivity : AppCompatActivity() {

    // MARK: - Properties
    private lateinit var binding: ActivityMainBinding

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
        loadFragment(HappyPlaceFragment())
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    // MARK: - Actions
    private fun setupAction() {
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.happy_place -> {
                    loadFragment(HappyPlaceFragment())
                    true
                }
                R.id.weather -> {
                    loadFragment(WeatherFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}