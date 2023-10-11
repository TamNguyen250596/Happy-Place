package com.example.happyplaces.module.modules.happy_place

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.module.adapters.HappyPlaceAdapter
import com.example.happyplaces.module.common.database.DatabaseHandler
import com.example.happyplaces.module.common.models.HappyPlaceModel
import com.happyplaces.utils.SwipeToDeleteCallback
import pl.kitek.rvswipetodelete.SwipeToEditCallback

class HappyPlacePresenter(
    private val view: HappyPlaceInterface.View,
    private val interaction: HappyPlaceInterface.Interaction,
    private val router: HappyPlaceInterface.Router
): HappyPlaceInterface.Presenter {

    // MARK: - Properties
    private var happyPlaces = ArrayList<HappyPlaceModel>()

    // MARK: - Functions
    override fun setupRecycleView(recyclerView: RecyclerView) {
        val context = recyclerView.context
        happyPlaces = interaction.getHappyPlacesList(context)

        val isHideRecyclerView = if (happyPlaces.isEmpty()) View.GONE else View.VISIBLE
        val isHidePlaceHolderView = if (happyPlaces.isNotEmpty()) View.GONE else View.VISIBLE

        view.showHideRecycleView(isHideRecyclerView)
        view.showHidePlaceHolderView(isHidePlaceHolderView)

        if (happyPlaces.isNotEmpty()) {

            recyclerView.layoutManager = LinearLayoutManager(context)
            var happyPlaceAdapter = recyclerView.adapter as? HappyPlaceAdapter
            if (happyPlaceAdapter == null) {
                happyPlaceAdapter = HappyPlaceAdapter(ArrayList(happyPlaces))
                recyclerView.adapter = happyPlaceAdapter
            } else {
                happyPlaceAdapter.reloadData(ArrayList(happyPlaces))
            }

            happyPlaceAdapter.setupHappyPlaceInterface(object : HappyPlaceAdapter.HappyPlaceAdapterInterface {
                override fun selectedItem(model: HappyPlaceModel) {
                    openHappyPlaceDetailActivity(model)
                }
            })

            val editSwipeHandler = object : SwipeToEditCallback(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    openAddHappyPlaceActivity(happyPlaces[viewHolder.absoluteAdapterPosition])
                    happyPlaceAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                }
            }
            val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
            editItemTouchHelper.attachToRecyclerView(recyclerView)

            val deleteSwipeHandler = object : SwipeToDeleteCallback(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val dbHandler = DatabaseHandler(context)
                    val model = happyPlaces[viewHolder.absoluteAdapterPosition]
                    val deleteHappyPlace = dbHandler.deleteHappyPlace(model.id)
                    if (deleteHappyPlace > 0) {
                        happyPlaceAdapter.removeItem(viewHolder.absoluteAdapterPosition)
                        setupRecycleView(recyclerView)
                    }
                }
            }
            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(recyclerView)
        }
    }

    // MARK: - Navigation
    private fun openHappyPlaceDetailActivity(model: HappyPlaceModel) {
        router.openHappyPlaceDetailActivity(model)
    }

    private fun openAddHappyPlaceActivity(model: HappyPlaceModel) {
        router.openAddHappyPlaceActivity(model)
    }
}