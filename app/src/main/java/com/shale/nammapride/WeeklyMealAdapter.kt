package com.shale.nammapride

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shale.nammapride.databinding.ItemWeeklyMealBinding

data class WeeklyMeal(
    val day: String,
    val name: String,
    val isToday: Boolean = false
)

class WeeklyMealAdapter(
    private val meals: List<WeeklyMeal>
) : RecyclerView.Adapter<WeeklyMealAdapter.MealViewHolder>() {

    inner class MealViewHolder(val binding: ItemWeeklyMealBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemWeeklyMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        holder.binding.tvDayBadge.text = meal.day
        holder.binding.tvMealName.text = meal.name

        if (meal.isToday) {
            holder.binding.tvTodayBadge.visibility = View.VISIBLE
            holder.binding.cardWeeklyMeal.strokeColor = Color.parseColor("#BBDEFB") // light blue border
            holder.binding.tvDayBadge.setBackgroundResource(R.drawable.circle_blue)
            holder.binding.tvDayBadge.setTextColor(Color.WHITE)
        } else {
            holder.binding.tvTodayBadge.visibility = View.GONE
            holder.binding.cardWeeklyMeal.strokeColor = Color.WHITE
            holder.binding.tvDayBadge.setBackgroundResource(R.drawable.pill_light_blue)
            holder.binding.tvDayBadge.setTextColor(Color.parseColor("#1976D2")) // primary blue
        }
    }

    override fun getItemCount() = meals.size
}
