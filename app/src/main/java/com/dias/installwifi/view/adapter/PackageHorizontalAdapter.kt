package com.dias.installwifi.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dias.installwifi.R
import com.dias.installwifi.databinding.ItemPackageBinding
import com.dias.installwifi.databinding.ItemPackageHorizontalBinding
import com.dias.installwifi.data.model.Package
import java.text.NumberFormat
import java.util.Locale

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

        val formattedPrice = NumberFormat.getInstance(Locale("in", "ID")).format(packageItem.price)

        if (holder is GridViewHolder) {
            holder.binding.apply {
                tvPackagePrice.text =
                    tvPackagePrice.context.getString(R.string.price, formattedPrice)
                Glide.with(ivPackage.context)
                    .load(packageItem.imageUrl)
                    .into(ivPackage)

            }
            holder.itemView.setOnClickListener { onItemClick(packageItem) }
        } else if (holder is HorizontalViewHolder) {
            holder.binding.apply {
                tvPackageName.text = packageItem.name
                tvPackageSpeed.text = tvPackageSpeed.context.getString(R.string.package_speed, packageItem.speed.toString())
                tvPackagePrice.text =
                    tvPackagePrice.context.getString(R.string.price, formattedPrice)
                Glide.with(ivPackage.context)
                    .load(packageItem.imageUrl)
                    .into(ivPackage)
            }
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
        this.isGrid = isGrid
        notifyDataSetChanged()
    }
}

