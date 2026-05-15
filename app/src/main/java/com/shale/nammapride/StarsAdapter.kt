package com.shale.nammapride

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.shale.nammapride.databinding.ItemStudentStarBinding

import coil.load

class StarsAdapter(
    private val stars: List<StudentStar>,
    private val isAdmin: Boolean = false,
    private val onPickImage: ((StudentStar) -> Unit)? = null
) : RecyclerView.Adapter<StarsAdapter.StarViewHolder>() {

    private val firebaseManager = FirebaseManager.getInstance()

    inner class StarViewHolder(val binding: ItemStudentStarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(star: StudentStar) {
            binding.studentName.text = star.name
            binding.studentAchievement.text = star.achievement
            
            // Load image
            if (star.imageUrl != null) {
                binding.studentImage.load(star.imageUrl)
            } else if (star.imageResId != null) {
                binding.studentImage.setImageResource(star.imageResId)
            }
            
            if (isAdmin) {
                binding.btnEditStar.visibility = View.VISIBLE
                binding.btnEditStar.setOnClickListener {
                    showEditDialog(star)
                }
            } else {
                binding.btnEditStar.visibility = View.GONE
            }
        }

        private fun showEditDialog(star: StudentStar) {
            val layout = android.widget.LinearLayout(itemView.context).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(40, 20, 40, 0)
            }
            
            val nameEdit = android.widget.EditText(itemView.context).apply {
                hint = "Student Name"
                setText(star.name)
            }
            val achievementEdit = android.widget.EditText(itemView.context).apply {
                hint = "Achievement"
                setText(star.achievement)
            }
            val descEdit = android.widget.EditText(itemView.context).apply {
                hint = "Description"
                setText(star.description)
            }
            val btnPickImage = android.widget.Button(itemView.context).apply {
                text = "Change Image"
                setOnClickListener {
                    onPickImage?.invoke(star)
                }
            }
            
            layout.addView(nameEdit)
            layout.addView(achievementEdit)
            layout.addView(descEdit)
            layout.addView(btnPickImage)

            AlertDialog.Builder(itemView.context)
                .setTitle("Edit Student Star")
                .setView(layout)
                .setPositiveButton(itemView.context.getString(R.string.save)) { _, _ ->
                    val newName = nameEdit.text.toString()
                    val newAchievement = achievementEdit.text.toString()
                    val newDesc = descEdit.text.toString()
                    if (newName.isNotEmpty() && newAchievement.isNotEmpty()) {
                        Toast.makeText(itemView.context, "Saving...", Toast.LENGTH_SHORT).show()
                        firebaseManager.updateStudentStar(star.copy(name = newName, achievement = newAchievement, description = newDesc)) {
                            Toast.makeText(itemView.context, "Saved!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton(itemView.context.getString(R.string.cancel), null)
                .show()
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
