package com.dias.installwifi.view.menu.packages

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.databinding.FragmentPackageBinding
import com.dias.installwifi.view.adapter.PackageHorizontalAdapter
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.view.detail.DetailPackageActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PackageFragment : Fragment() {

    private var _binding: FragmentPackageBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewPackage: RecyclerView
    private lateinit var packageAdapter: PackageHorizontalAdapter

    private val viewModel: PackageViewModel by viewModels()

    private var isGridMode = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPackageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupPackage()

        viewModel.getPackages()
        observePackageResult()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("toggle_layout") { _, bundle ->
            val isGrid = bundle.getBoolean("isGrid", false)
            isGridMode = isGrid
            if (this::recyclerViewPackage.isInitialized && this::packageAdapter.isInitialized) {
                recyclerViewPackage.layoutManager = if (isGridMode) {
                    GridLayoutManager(requireContext(), 2)
                } else {
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                }
                packageAdapter.setGridMode(isGridMode)
            }
        }
    }

    private fun setupPackage() {
        recyclerViewPackage = binding.rvPackage
        recyclerViewPackage.layoutManager = if (isGridMode) {
            GridLayoutManager(requireContext(), 2)
        } else {
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        packageAdapter = PackageHorizontalAdapter({ selectedPackage ->
            navigateToDetail(selectedPackage.id)
        }, isGridMode)
        recyclerViewPackage.adapter = packageAdapter
        packageAdapter.setGridMode(isGridMode)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observePackageResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPackageResult.collect { result ->
                    when (result) {
                        is ResultState.Loading -> showLoading(binding.loading, true)

                        is ResultState.Success -> {
                            showLoading(binding.loading, false)
                            packageAdapter.updateData(result.data)
                        }

                        is ResultState.Error -> {
                            showLoading(binding.loading, false)
                            if (result.error.isNotEmpty()) {
                                binding.tvEmpty.text = result.error
                                binding.tvEmpty.visibility = View.VISIBLE
                            } else {
                                binding.tvEmpty.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToDetail(packageId: Int) {
        val intent = Intent(requireContext(), DetailPackageActivity::class.java).apply {
            putExtra(DetailPackageActivity.EXTRA_PACKAGE_ID, packageId)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

