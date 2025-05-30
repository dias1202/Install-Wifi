package com.dias.installwifi.view.info.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dias.installwifi.data.model.NotificationList
import com.dias.installwifi.databinding.FragmentNotificationBinding
import com.dias.installwifi.view.adapter.NotificationAdapter

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewNotification: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (NotificationList.isEmpty()) {
            binding.apply {
                tvEmptyNotification.visibility = View.VISIBLE
                rvNotification.visibility = View.GONE
            }
        } else {
            binding.apply {
                tvEmptyNotification.visibility = View.GONE
                rvNotification.visibility = View.VISIBLE
            }
            setupNotification()
        }

        return root
    }

    private fun setupNotification() {
        recyclerViewNotification = binding.rvNotification
        recyclerViewNotification.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        )

        val notificationAdapter = NotificationAdapter(NotificationList)
        recyclerViewNotification.adapter = notificationAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}