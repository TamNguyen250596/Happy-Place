package com.example.happyplaces.module.modules.happy_place.happy_place_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.happyplaces.databinding.FragmentHappyListPlaceBinding
import com.example.happyplaces.module.modules.happy_place.add_happy_place.AddHappyPlaceActivity

class HappyPlaceListFragment : Fragment(), HappyPlaceListInterface.View {

    // MARK: - Properties
    private lateinit var presenter: HappyPlaceListInterface.Presenter
    private lateinit var binding: FragmentHappyListPlaceBinding
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
        val router = HappyPlaceListRouter()
        val interaction = HappyPlaceListInteraction()
        val presenter = HappyPlaceListPresenter()
        router.fragment = this
        presenter.view = this
        presenter.router = router
        presenter.interaction = interaction
        this.presenter = presenter
        binding = FragmentHappyListPlaceBinding.inflate(inflater, container, false)
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


