package com.dias.installwifi.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dias.installwifi.databinding.ItemPackageHorizontalBinding
import com.dias.installwifi.data.model.Package

class PackageHorizontalAdapter(
    private val packages: List<Package>,
    private val onItemClick: (Package) -> Unit
) :
    RecyclerView.Adapter<PackageHorizontalAdapter.PackageViewHolder>() {

    class PackageViewHolder(val binding: ItemPackageHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PackageViewHolder {
        val binding = ItemPackageHorizontalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val packageItem = packages[position]
        holder.binding.apply {
            ivPackage.setImageResource(packageItem.image)
            tvPackageName.text = packageItem.name
            tvPackageSpeed.text = "${packageItem.speed} Mbps"
            tvPackagePrice.text = "Rp ${packageItem.price}"
        }

        holder.itemView.setOnClickListener {
            onItemClick(packageItem)
        }
    }

    override fun getItemCount(): Int = packages.size
}