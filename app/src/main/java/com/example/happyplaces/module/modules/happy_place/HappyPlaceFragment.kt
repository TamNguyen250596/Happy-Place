package com.example.happyplaces.module.modules.happy_place

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.happyplaces.databinding.FragmentHappyPlaceBinding
import com.example.happyplaces.module.modules.AddHappyPlaceActivity

class HappyPlaceFragment : Fragment(), HappyPlaceInterface.View {

    // MARK: - Properties
    private lateinit var presenter: HappyPlaceInterface.Presenter
    private lateinit var interaction: HappyPlaceInteraction
    private lateinit var router: HappyPlaceRouter
    private lateinit var binding: FragmentHappyPlaceBinding
    lateinit var startForResult: ActivityResultLauncher<Intent>

    // MARK: - Companion
    companion object {
        const val didSaveHappyPlace = 100
        const val putHappyPlace = "put_happy_place"
    }

    // MARK: - Life cycle
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        router = HappyPlaceRouter(this)
        interaction = HappyPlaceInteraction()
        presenter = HappyPlacePresenter(this, interaction, router)
        binding = FragmentHappyPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setupRecycleView(binding.recyclerView)
        implementAction()
        setupStartForResult()
    }

    // MARK: - Functions
    private fun implementAction() {
        binding.fabAddHappyPlace.setOnClickListener {
            val intent = Intent(activity, AddHappyPlaceActivity::class.java)
            startForResult.launch(intent)
        }
    }
    private fun setupStartForResult() {
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == didSaveHappyPlace) {
                    presenter.setupRecycleView(binding.recyclerView)
                }
            }
    }
    override fun showHideRecycleView(isGone: Int) {
        binding.recyclerView.visibility = isGone
    }

    override fun showHidePlaceHolderView(isGone: Int) {
        binding.placeHolderTextView.visibility = isGone
    }
}


