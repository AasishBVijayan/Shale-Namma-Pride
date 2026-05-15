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
            val layout = android.widget.LinearLayout(itemView.context).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(40, 20, 40, 0)
            }

            val nameEdit = android.widget.EditText(itemView.context).apply {
                hint = "Facility Name"
                setText(facility.name)
            }
            val descEdit = android.widget.EditText(itemView.context).apply {
                hint = "Description"
                setText(facility.description)
            }
            val btnPickImage = android.widget.Button(itemView.context).apply {
                text = "Change Image"
                setOnClickListener {
                    onPickImage?.invoke(facility)
                }
            }

            layout.addView(nameEdit)
            layout.addView(descEdit)
            layout.addView(btnPickImage)

            AlertDialog.Builder(itemView.context)
                .setTitle("Edit Facility")
                .setView(layout)
                .setPositiveButton(itemView.context.getString(R.string.save)) { _, _ ->
                    val newName = nameEdit.text.toString()
                    val newDesc = descEdit.text.toString()
                    if (newName.isNotEmpty()) {
                        Toast.makeText(itemView.context, "Saving...", Toast.LENGTH_SHORT).show()
                        firebaseManager.updateFacility(facility.copy(name = newName, description = newDesc)) {
                            Toast.makeText(itemView.context, "Saved!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton(itemView.context.getString(R.string.cancel), null)
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
