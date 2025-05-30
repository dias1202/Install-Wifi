package com.dias.installwifi.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dias.installwifi.view.info.news.NewsFragment
import com.dias.installwifi.view.info.notification.NotificationFragment

class TabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NotificationFragment()
            1 -> NewsFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
