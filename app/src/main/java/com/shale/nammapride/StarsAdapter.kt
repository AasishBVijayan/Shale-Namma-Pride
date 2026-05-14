package com.shale.nammapride

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.shale.nammapride.databinding.ItemStudentStarBinding

class StarsAdapter(
    private val stars: List<StudentStar>,
    private val isAdmin: Boolean = false
) : RecyclerView.Adapter<StarsAdapter.StarViewHolder>() {

    inner class StarViewHolder(val binding: ItemStudentStarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(star: StudentStar) {
            binding.studentName.text = star.name
            binding.studentAchievement.text = star.achievement
            
            if (isAdmin) {
                binding.btnEditStar.visibility = View.VISIBLE
                binding.btnEditStar.setOnClickListener {
                    AlertDialog.Builder(itemView.context)
                        .setTitle("Edit Student Star")
                        .setMessage("Admin editing options for ${star.name} would appear here.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            } else {
                binding.btnEditStar.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarViewHolder {
        val binding = ItemStudentStarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StarViewHolder, position: Int) {
        holder.bind(stars[position])
    }

    override fun getItemCount(): Int = stars.size
}
