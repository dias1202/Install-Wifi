package com.dias.installwifi.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dias.installwifi.data.model.Notification
import com.dias.installwifi.databinding.ItemNotificationBinding
import com.dias.installwifi.view.adapter.NotificationAdapter.NotificationViewHolder

class NotificationAdapter(private val notification: List<Notification>) :
    RecyclerView.Adapter<NotificationViewHolder>() {
    class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notification[position]
        holder.binding.apply {
            tvNotificationTitle.text = notification.title
            tvNotificationContent.text = notification.content
            tvNotificationTime.text = notification.timestamp.toString()
        }
    }

    override fun getItemCount(): Int = notification.size
}