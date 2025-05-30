package com.dias.installwifi.view.menu.`package`

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dias.installwifi.data.model.Packages
import com.dias.installwifi.databinding.FragmentPackageBinding
import com.dias.installwifi.view.adapter.PackageHorizontalAdapter
import com.dias.installwifi.data.model.Package
import com.dias.installwifi.view.detail.DetailActivity

class PackageFragment : Fragment() {

    private var _binding: FragmentPackageBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewPackage: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPackageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupPackage()

        return root
    }

    private fun setupPackage() {
        recyclerViewPackage = binding.rvPackage
        recyclerViewPackage.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        )

        val packageAdapter = PackageHorizontalAdapter(Packages) { selectedPackage ->

        }

        recyclerViewPackage.adapter = packageAdapter
    }

    private fun navigateToDetail() {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}