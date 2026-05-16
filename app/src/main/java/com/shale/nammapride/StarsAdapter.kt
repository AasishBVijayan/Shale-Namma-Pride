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
            val context = itemView.context
            val dialogBinding = com.shale.nammapride.databinding.DialogEditGenericBinding.inflate(
                android.view.LayoutInflater.from(context)
            )

            dialogBinding.tvDialogTitle.text = "Edit Student Star"
            dialogBinding.etName.setText(star.name)
            dialogBinding.tilName.hint = "Student Name"

            dialogBinding.tilExtra1.visibility = View.VISIBLE
            dialogBinding.tilExtra1.hint = "Achievement"
            dialogBinding.etExtra1.setText(star.achievement)

            dialogBinding.tilExtra2.visibility = View.VISIBLE
            dialogBinding.tilExtra2.hint = "Description"
            dialogBinding.etExtra2.setText(star.description)

            if (star.imageUrl != null) {
                dialogBinding.ivPreview.load(star.imageUrl)
            } else if (star.imageResId != null) {
                dialogBinding.ivPreview.setImageResource(star.imageResId)
            }

            dialogBinding.btnChangeImage.setOnClickListener {
                onPickImage?.invoke(star)
            }

            AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .setPositiveButton(context.getString(R.string.save)) { _, _ ->
                    val newName = dialogBinding.etName.text.toString()
                    val newAchievement = dialogBinding.etExtra1.text.toString()
                    val newDesc = dialogBinding.etExtra2.text.toString()
                    if (newName.isNotEmpty() && newAchievement.isNotEmpty()) {
                        firebaseManager.updateStudentStar(star.copy(name = newName, achievement = newAchievement, description = newDesc)) {
                            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton(context.getString(R.string.cancel), null)
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
