package com.example.happyplaces.module.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.databinding.ItemHappyPlcaeBinding
import com.example.happyplaces.module.common.models.HappyPlaceModel

class HappyPlaceAdapter(private var items: ArrayList<HappyPlaceModel>): RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>() {

    // MARK: - Properties
    var happyPlaceInterface: HappyPlaceAdapterInterface? = null

    // MARK: - Adapter cycle
    class ViewHolder(private val binding: ItemHappyPlcaeBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.ivPlaceImage
        val titleTextView = binding.tvTitle
        val descriptionTextView = binding.tvDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHappyPlcaeBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.imageView.setImageURI(item.image.toUri())
        holder.titleTextView.text = item.title
        holder.descriptionTextView.text = item.description
        holder.itemView.setOnClickListener {
            val happyPlaceInterface = happyPlaceInterface ?: return@setOnClickListener
            happyPlaceInterface.selectedItem(item)
        }
    }

    // MARK: - Interface
    interface HappyPlaceAdapterInterface {
        fun selectedItem(model: HappyPlaceModel)
    }

    fun setupHappyPlaceInterface(interfaceObject: HappyPlaceAdapterInterface) {
        happyPlaceInterface = interfaceObject
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun reloadData(newItems: ArrayList<HappyPlaceModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}