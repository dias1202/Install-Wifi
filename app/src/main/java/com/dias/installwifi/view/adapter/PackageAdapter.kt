package com.dias.installwifi.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dias.installwifi.databinding.ItemPackageBinding
import com.dias.installwifi.data.model.Package

class PackageAdapter(private val packages: List<Package>) :
    RecyclerView.Adapter<PackageAdapter.PackageViewHolder>() {

    class PackageViewHolder(val binding: ItemPackageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PackageViewHolder {
        val binding = ItemPackageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val packageItem = packages[position]
        holder.binding.apply {
            ivPackage.setImageResource(packageItem.image)
            tvPackagePrice.text = "Rp ${packageItem.price}"
        }
    }

    override fun getItemCount(): Int = packages.size
}