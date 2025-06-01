package com.dias.installwifi.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dias.installwifi.data.model.Banner
import com.dias.installwifi.databinding.ItemBannerBinding

class BannerAdapter(
    private val onItemClick: (Banner) -> Unit
) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    private var bannerList: List<Banner> = listOf()

    class BannerViewHolder(val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = bannerList[position]
        holder.binding.apply {
            carouselImageView.contentDescription = banner.description

            Glide.with(carouselImageView.context)
                .load(banner.imageUrl)
                .into(carouselImageView)
        }

        holder.itemView.setOnClickListener {
            onItemClick(banner)
        }
    }

    override fun getItemCount(): Int = bannerList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Banner>) {
        bannerList = newData
        notifyDataSetChanged()
    }
}