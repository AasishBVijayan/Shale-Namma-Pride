package com.shale.nammapride

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.shale.nammapride.databinding.ItemFacilityBinding

import coil.load

class FacilityAdapter(
    private val facilities: List<Facility>,
    private val isAdmin: Boolean = false,
    private val onPickImage: ((Facility) -> Unit)? = null
) : RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder>() {

    private val firebaseManager = FirebaseManager.getInstance()

    inner class FacilityViewHolder(val binding: ItemFacilityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(facility: Facility) {
            binding.facilityName.text = facility.name
            binding.facilityDesc.text = facility.description
            
            // Load image
            if (facility.imageUrl != null) {
                binding.facilityImage.load(facility.imageUrl)
            } else if (facility.imageResId != null) {
                binding.facilityImage.setImageResource(facility.imageResId)
            }
            
            if (isAdmin) {
                binding.btnEditFacility.visibility = View.VISIBLE
                binding.btnEditFacility.setOnClickListener {
                    showEditDialog(facility)
                }
            } else {
                binding.btnEditFacility.visibility = View.GONE
            }
        }

        private fun showEditDialog(facility: Facility) {
            val context = itemView.context
            val dialogBinding = com.shale.nammapride.databinding.DialogEditGenericBinding.inflate(
                android.view.LayoutInflater.from(context)
            )

            dialogBinding.tvDialogTitle.text = "Edit Facility"
            dialogBinding.etName.setText(facility.name)
            dialogBinding.tilName.hint = "Facility Name"

            dialogBinding.tilExtra1.visibility = View.VISIBLE
            dialogBinding.tilExtra1.hint = "Description"
            dialogBinding.etExtra1.setText(facility.description)

            if (facility.imageUrl != null) {
                dialogBinding.ivPreview.load(facility.imageUrl)
            } else if (facility.imageResId != null) {
                dialogBinding.ivPreview.setImageResource(facility.imageResId)
            }

            dialogBinding.btnChangeImage.setOnClickListener {
                onPickImage?.invoke(facility)
            }

            AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .setPositiveButton(context.getString(R.string.save)) { _, _ ->
                    val newName = dialogBinding.etName.text.toString()
                    val newDesc = dialogBinding.etExtra1.text.toString()
                    if (newName.isNotEmpty()) {
                        firebaseManager.updateFacility(facility.copy(name = newName, description = newDesc)) {
                            Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton(context.getString(R.string.cancel), null)
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val binding = ItemFacilityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FacilityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        holder.bind(facilities[position])
    }

    override fun getItemCount(): Int = facilities.size
}
