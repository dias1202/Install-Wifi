package com.dias.installwifi.view.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dias.installwifi.databinding.ItemPackageBinding
import com.dias.installwifi.databinding.ItemPackageHorizontalBinding
import com.dias.installwifi.data.model.Package

class PackageHorizontalAdapter(
    private val onItemClick: (Package) -> Unit,
    private var isGrid: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var packageList: List<Package> = listOf()

    companion object {
        private const val VIEW_TYPE_HORIZONTAL = 0
        private const val VIEW_TYPE_GRID = 1
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("PackageAdapter", "getItemViewType: isGrid=$isGrid, position=$position")
        return if (isGrid) VIEW_TYPE_GRID else VIEW_TYPE_HORIZONTAL
    }

    inner class HorizontalViewHolder(val binding: ItemPackageHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class GridViewHolder(val binding: ItemPackageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_GRID) {
            val binding =
                ItemPackageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GridViewHolder(binding)
        } else {
            val binding = ItemPackageHorizontalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            HorizontalViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val packageItem = packageList[position]
        if (holder is GridViewHolder) {
            Log.d("PackageAdapter", "onBindViewHolder: GridViewHolder at $position")
            holder.binding.tvPackagePrice.text = "Rp ${packageItem.price}"
            Glide.with(holder.binding.ivPackage.context)
                .load(packageItem.imageUrl)
                .into(holder.binding.ivPackage)
            holder.itemView.setOnClickListener { onItemClick(packageItem) }
        } else if (holder is HorizontalViewHolder) {
            Log.d("PackageAdapter", "onBindViewHolder: HorizontalViewHolder at $position")
            holder.binding.tvPackageName.text = packageItem.name
            holder.binding.tvPackageSpeed.text = "${packageItem.speed} Mbps"
            holder.binding.tvPackagePrice.text = "Rp ${packageItem.price}"
            Glide.with(holder.binding.ivPackage.context)
                .load(packageItem.imageUrl)
                .into(holder.binding.ivPackage)
            holder.itemView.setOnClickListener { onItemClick(packageItem) }
        }
    }

    override fun getItemCount(): Int = packageList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Package>) {
        packageList = newData
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setGridMode(isGrid: Boolean) {
        Log.d("PackageAdapter", "setGridMode: $isGrid")
        this.isGrid = isGrid
        notifyDataSetChanged()
    }
}

