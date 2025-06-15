package com.dias.installwifi.view.technician.menu.job

import JobAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dias.installwifi.R
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.databinding.FragmentJobBinding
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.view.viewmodel.AuthViewModel
import com.dias.installwifi.view.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JobFragment : Fragment() {

    private var _binding: FragmentJobBinding? = null
    private val binding get() = _binding!!

    private val jobAdapter by lazy { JobAdapter() }
    private val authViewModel: AuthViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()
    private val loading by lazy { binding.loading }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobBinding.inflate(inflater, container, false)

        authViewModel.getSession()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeTechnicianSession()
        observeOrderResult()
    }

    private fun setupRecyclerView() {
        binding.rvJob.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = jobAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeTechnicianSession() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.getSessionResult.collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            orderViewModel.getOrdersByTechnicianId(result.data.uid)
                        }
                        is ResultState.Error -> {
                            showLoading(loading, false)
                            showErrorState(result.error)
                        }
                        ResultState.Loading -> showLoading(loading, true)
                    }
                }
            }
        }
    }

    private fun observeOrderResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.getOrdersByTechnicianIdResult.collect { result ->
                    when (result) {
                        is ResultState.Loading -> showLoading(loading, true)
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            if (result.data.isNotEmpty()) {
                                jobAdapter.updateData(result.data)
                                showDataState()
                            } else {
                                showEmptyState(getString(R.string.no_jobs_available))
                            }
                        }
                        is ResultState.Error -> {
                            showLoading(loading, false)
                            showErrorState(result.error)
                        }
                    }
                }
            }
        }
    }

    private fun showDataState() {
        binding.apply {
            rvJob.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyState(message: String) {
        binding.apply {
            rvJob.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
            tvEmpty.text = message
        }
    }

    private fun showErrorState(error: String) {
        binding.apply {
            rvJob.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
            tvEmpty.text = error
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}