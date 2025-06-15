package com.dias.installwifi.view.user.menu.home

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.recyclerview.widget.RecyclerView
import com.dias.installwifi.R
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.databinding.FragmentHomeBinding
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import com.dias.installwifi.view.adapter.BannerAdapter
import com.dias.installwifi.view.adapter.PackageAdapter
import com.dias.installwifi.view.user.detail.DetailBannerActivity
import com.dias.installwifi.view.user.detail.DetailPackageActivity
import com.dias.installwifi.view.viewmodel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewBanner: RecyclerView
    private lateinit var recyclerViewPackages: RecyclerView
    private lateinit var packageAdapter: PackageAdapter
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var bannerPromoAdapter: BannerAdapter

    private val loading by lazy { binding.loading }

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.apply {
            getPackages()
            getBanners()
            getBannerPromo()
        }

        setupBanner()
        setupPackages()
        setupBannerPromo()

        observePackageResult()
        observeBannerResult()
        observeBannerPromoResult()

        binding.apply {
            tvSeeAll.setOnClickListener {
                val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
                navBar.selectedItemId = R.id.navigation_package
            }
        }

        return root
    }

    private fun setupBanner() {
        recyclerViewBanner = binding.rvBanner
        recyclerViewBanner.setLayoutManager(CarouselLayoutManager())

        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewBanner)

        bannerAdapter = BannerAdapter {
            // Handle banner click if needed
        }
        recyclerViewBanner.adapter = bannerAdapter
    }

    private fun observeBannerResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getBannerResult.collect { result ->
                    when (result) {
                        is ResultState.Loading -> showLoading(loading, true)
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            bannerAdapter.updateData(result.data)
                        }

                        is ResultState.Error -> {
                            showLoading(loading, false)
                            showToast(requireContext(), result.error)
                        }
                    }
                }
            }
        }
    }

    private fun setupPackages() {
        recyclerViewPackages = binding.rvPackageOptions
        recyclerViewPackages.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        packageAdapter = PackageAdapter { selectedPackage ->
            navigateToPackageDetails(selectedPackage.id)
        }
        recyclerViewPackages.adapter = packageAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observePackageResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPackageResult.collect { result ->
                    when (result) {
                        is ResultState.Loading -> showLoading(loading, true)

                        is ResultState.Success -> {
                            showLoading(loading, false)
                            packageAdapter.updateData(result.data.subList(0, 4))
                        }

                        is ResultState.Error -> {
                            showLoading(loading, false)
                            showToast(requireContext(), result.error)
                        }
                    }
                }
            }
        }
    }

    private fun setupBannerPromo() {
        recyclerViewBanner = binding.rvBannerPromo
        recyclerViewBanner.setLayoutManager(CarouselLayoutManager())

        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewBanner)

        bannerPromoAdapter = BannerAdapter {
            navigateToBannerDetails(it.id ?: 0)
        }
        recyclerViewBanner.adapter = bannerPromoAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeBannerPromoResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getBannerPromoResult.collect { result ->
                    when (result) {
                        is ResultState.Loading -> showLoading(loading, true)

                        is ResultState.Success -> {
                            showLoading(loading, false)
                            bannerPromoAdapter.updateData(result.data)
                        }

                        is ResultState.Error -> {
                            showLoading(loading, false)
                            showToast(requireContext(), result.error)
                        }
                    }
                }
            }
        }
    }

    private fun navigateToPackageDetails(packageId: Int) {
        val intent = Intent(requireContext(), DetailPackageActivity::class.java).apply {
            putExtra(DetailPackageActivity.EXTRA_PACKAGE_ID, packageId)
        }
        startActivity(intent)
    }

    private fun navigateToBannerDetails(bannerId: Int) {
        val intent = Intent(requireContext(), DetailBannerActivity::class.java).apply {
            putExtra(DetailBannerActivity.EXTRA_BANNER_ID, bannerId)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
