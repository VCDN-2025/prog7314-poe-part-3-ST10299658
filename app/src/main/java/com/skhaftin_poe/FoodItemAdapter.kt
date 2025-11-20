package com.skhaftin_poe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load // Simple import!
import coil.transform.RoundedCornersTransformation

class FoodItemsAdapter : ListAdapter<FoodItem, FoodItemsAdapter.FoodItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return FoodItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val foodItem = getItem(position)
        holder.bind(foodItem)
    }

    class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvExpiry: TextView = itemView.findViewById(R.id.tvExpiry)
        private val tvUrgency: TextView = itemView.findViewById(R.id.tvUrgency)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        private val ivFoodImage: ImageView = itemView.findViewById(R.id.ivFoodImage)

        fun bind(foodItem: FoodItem) {
            tvFoodName.text = foodItem.foodName
            tvDescription.text = foodItem.description
            tvCategory.text = foodItem.category
            tvExpiry.text = "Expires: ${foodItem.expiryDate}"
            tvUrgency.text = "Urgency: ${foodItem.urgency}"
            tvQuantity.text = "Quantity: ${foodItem.quantity}"

            // Load image with Coil - much simpler!
            foodItem.imageUrl?.let { url ->
                ivFoodImage.load(url) {
                    crossfade(true)
                    placeholder(R.drawable.logo)
                    error(R.drawable.bread)
                    transformations(RoundedCornersTransformation(16f))
                }
            } ?: run {
                ivFoodImage.setImageResource(R.drawable.logo)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem.foodId == newItem.foodId
        }

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem == newItem
        }
    }
}