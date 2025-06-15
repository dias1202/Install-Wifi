package com.dias.installwifi.view.user.menu.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.databinding.FragmentHistoryBinding
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.view.adapter.HistoryAdapter
import com.dias.installwifi.view.viewmodel.AuthViewModel
import com.dias.installwifi.view.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()

    private lateinit var historyAdapter: HistoryAdapter  // Declare as class property
    private val loading by lazy { binding.loading }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.getSession()
        setupRecyclerView()
        observeUserSession()
        observeOrderResult()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()  // Initialize the class property

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = historyAdapter
        }
    }

    private fun observeUserSession() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.getSessionResult.collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            orderViewModel.getOrdersByUserId(result.data.uid)
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeOrderResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.getOrderByUserId.collect { result ->
                    when (result) {
                        is ResultState.Loading -> showLoading(loading, true)
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            historyAdapter.updateData(result.data)
                            if (result.data.isEmpty()) {
                                binding.apply {
                                    tvEmptyHistory.visibility = View.VISIBLE
                                    rvHistory.visibility = View.GONE
                                }
                            }
                        }
                        is ResultState.Error -> {
                            showLoading(loading, false)
                            binding.apply {
                                tvEmptyHistory.visibility = View.VISIBLE
                                rvHistory.visibility = View.GONE
                                tvEmptyHistory.text = result.error
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}