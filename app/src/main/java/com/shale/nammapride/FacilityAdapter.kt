package com.shale.nammapride

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.shale.nammapride.databinding.ItemFacilityBinding

class FacilityAdapter(
    private val facilities: List<Facility>,
    private val isAdmin: Boolean = false
) : RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder>() {

    inner class FacilityViewHolder(val binding: ItemFacilityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(facility: Facility) {
            binding.facilityName.text = facility.name
            
            if (isAdmin) {
                binding.btnEditFacility.visibility = View.VISIBLE
                binding.btnEditFacility.setOnClickListener {
                    AlertDialog.Builder(itemView.context)
                        .setTitle("Edit Facility")
                        .setMessage("Admin editing options for ${facility.name} would appear here.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            } else {
                binding.btnEditFacility.visibility = View.GONE
            }
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
