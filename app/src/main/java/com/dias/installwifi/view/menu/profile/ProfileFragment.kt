package com.dias.installwifi.view.menu.profile

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
import com.dias.installwifi.R
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.databinding.FragmentProfileBinding
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import com.dias.installwifi.view.authentication.AuthActivity
import com.dias.installwifi.view.authentication.AuthViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var loading: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        authViewModel.getSession()

        loading = binding.loading

        binding.apply {
            tvLogout.setOnClickListener {
                logout()
            }
            tvMyProfile.setOnClickListener {
                openDialogProfile()
            }
            tvSettings.setOnClickListener {
                openDialogSetting()
            }
        }

        observerSession()

        return view
    }

    private fun observerSession() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.getSessionResult.collect {
                    when (it) {
                        ResultState.Loading -> showLoading(loading, true)
                        is ResultState.Success -> {
                            showLoading(loading, false)
//                            Glide.with(requireContext())
//                                .load(it.data.photoUrl)
//                                .error(R.drawable.saya1)
//                                .into(binding.civProfile)

//                            binding.apply {
//                                tvName.text = it.data.name
//                                tvEmail.text = it.data.email
//                            }
                        }

                        is ResultState.Error -> {
                            showLoading(loading, false)
                            showToast(requireContext(), it.error)
                        }
                    }
                }
            }
        }
    }

    private fun openDialogProfile() {
        MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.custom_dialog_profile)
            .setCancelable(true)
            .show()
    }

    private fun openDialogSetting() {
        MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.custom_dialog_setting)
            .setTitle("Setting")
            .setCancelable(true)
            .show()
    }

    private fun logout() {
        authViewModel.logout()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}