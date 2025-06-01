package com.dias.installwifi.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dias.installwifi.databinding.ItemPackageBinding
import com.dias.installwifi.data.model.Package

class PackageAdapter(
    private val onItemClick: (Package) -> Unit
) :
    RecyclerView.Adapter<PackageAdapter.PackageViewHolder>() {

    private var packageList: List<Package> = listOf()

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
        val packageItem = packageList[position]
        holder.binding.apply {
            tvPackagePrice.text = "Rp ${packageItem.price}"

            Glide.with(ivPackage.context)
                .load(packageItem.imageUrl)
                .into(ivPackage)
        }

        holder.itemView.setOnClickListener {
            onItemClick(packageItem)
        }
    }

    override fun getItemCount(): Int = packageList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Package>) {
        packageList = newData
        notifyDataSetChanged()
    }
}