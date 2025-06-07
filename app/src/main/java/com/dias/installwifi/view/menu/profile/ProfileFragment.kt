package com.dias.installwifi.view.menu.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dias.installwifi.R
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.User
import com.dias.installwifi.databinding.CustomDialogProfileBinding
import com.dias.installwifi.databinding.FragmentProfileBinding
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import com.dias.installwifi.view.authentication.AuthActivity
import com.dias.installwifi.view.viewmodel.AuthViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import com.dias.installwifi.data.local.pref.ThemePreferenceDataStore
import com.dias.installwifi.utils.NotificationPreferenceDataStore
import com.dias.installwifi.utils.setupDropdown
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    private var profileDialog: AlertDialog? = null

    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        authViewModel.getSession()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdown(
            context = requireContext(),
            lifecycleScope = viewLifecycleOwner.lifecycleScope,
            dropdown = binding.tvToogleTheme,
            options = listOf(getString(R.string.system_theme), getString(R.string.light), getString(R.string.dark)),
            getValue = { ThemePreferenceDataStore.getTheme(requireContext()).first() },
            setValue = { ThemePreferenceDataStore.setTheme(requireContext(), it) },
            mapToIndex = { mode -> when (mode) { AppCompatDelegate.MODE_NIGHT_NO -> 1; AppCompatDelegate.MODE_NIGHT_YES -> 2; else -> 0 } },
            mapFromIndex = { idx -> when (idx) { 1 -> AppCompatDelegate.MODE_NIGHT_NO; 2 -> AppCompatDelegate.MODE_NIGHT_YES; else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM } }
        )
        setupDropdown(
            context = requireContext(),
            lifecycleScope = viewLifecycleOwner.lifecycleScope,
            dropdown = binding.stateNotification,
            options = listOf(getString(R.string.allow), getString(R.string.disallow)),
            getValue = { NotificationPreferenceDataStore.getNotificationEnabled(requireContext()).first() },
            setValue = { NotificationPreferenceDataStore.setNotificationEnabled(requireContext(), it) },
            mapToIndex = { enabled -> if (enabled) 0 else 1 },
            mapFromIndex = { idx -> idx == 0 }
        )
        setupProfileActions()
    }

    private fun setupProfileActions() {
        binding.apply {
            tvLogout.setOnClickListener { logout() }
            tvMyProfile.setOnClickListener { openDialogProfile() }
        }
    }

    private fun observeSession(dialogBinding: CustomDialogProfileBinding) {
        val loading = dialogBinding.loading
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.getSessionResult.collect {
                    when (it) {
                        ResultState.Loading -> showLoading(loading, true)
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            currentUser = it.data
                            Glide.with(requireContext())
                                .load(currentUser?.photoUrl)
                                .placeholder(R.drawable.ic_person_32dp)
                                .error(R.drawable.ic_person_32dp)
                                .into(dialogBinding.civProfile)
                            dialogBinding.apply {
                                tvValueName.text = currentUser?.name
                                tvValueEmailAccount.text = currentUser?.email
                                tvValueMobileNumber.text =
                                    if (currentUser?.phoneNumber.isNullOrBlank()) getString(
                                        R.string.my_mobile_number
                                    ) else currentUser?.phoneNumber
                                tvValueLocation.text =
                                    if (currentUser?.address.isNullOrBlank()) getString(
                                        R.string.add_location
                                    ) else currentUser?.address
                            }
                            dialogBinding.buttonEdit.setOnClickListener {
                                navigateToEditProfile()
                            }
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
        val dialogBinding = CustomDialogProfileBinding.inflate(layoutInflater)

        profileDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .show()

        dialogBinding.buttonEdit.setOnClickListener {
            navigateToEditProfile()
        }

        observeSession(dialogBinding)
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

    private fun navigateToEditProfile() {
        profileDialog?.dismiss()
        val action = ProfileFragmentDirections.actionNavigationProfileToEditProfileFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
