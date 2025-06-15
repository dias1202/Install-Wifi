package com.dias.installwifi.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dias.installwifi.data.model.Order
import com.dias.installwifi.databinding.ItemHistoryBinding
import com.dias.installwifi.view.adapter.HistoryAdapter.HistoryViewHolder

class HistoryAdapter() :
    RecyclerView.Adapter<HistoryViewHolder>() {

    private var historyList: List<Order> = listOf()

    class HistoryViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.binding.apply {
            tvOrderTitle.text = history.id
            tvOrderContent.text = history.packageId
            tvOrderTime.text = history.orderDate.toString()
        }
    }

    override fun getItemCount(): Int = historyList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Order>) {
        historyList = newData
        notifyDataSetChanged()
    }
}