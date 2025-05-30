package com.dias.installwifi.view.menu.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dias.installwifi.R
import com.dias.installwifi.data.model.BannerPromo
import com.dias.installwifi.data.model.Banners
import com.dias.installwifi.data.model.Packages
import com.dias.installwifi.databinding.FragmentHomeBinding
import com.dias.installwifi.view.adapter.BannerAdapter
import com.dias.installwifi.view.adapter.PackageAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.UncontainedCarouselStrategy

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewBanner: RecyclerView
    private lateinit var recyclerViewPackages: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupBanner()
        setupPackages()
        setupBannerPromo()

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

        val bannerAdapter = BannerAdapter(Banners)
        recyclerViewBanner.adapter = bannerAdapter
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

        val packageAdapter = PackageAdapter(Packages)
        recyclerViewPackages.adapter = packageAdapter
    }

    private fun setupBannerPromo() {
        recyclerViewBanner = binding.rvBannerPromo
        recyclerViewBanner.setLayoutManager(CarouselLayoutManager())

        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewBanner)

        val bannerAdapter = BannerAdapter(BannerPromo)
        recyclerViewBanner.adapter = bannerAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
