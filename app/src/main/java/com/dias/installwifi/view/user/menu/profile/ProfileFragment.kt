package com.dias.installwifi.view.user.menu.profile

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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dias.installwifi.R
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.User
import com.dias.installwifi.databinding.FragmentProfileBinding
import com.dias.installwifi.utils.States.showToast
import com.dias.installwifi.view.authentication.AuthActivity
import com.dias.installwifi.view.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        authViewModel.getSession()

        binding.apply {
            tvLogout.setOnClickListener { logout() }
            buttonEdit.setOnClickListener { navigateToEditProfile() }
        }

        observeSession()

        return view
    }

    private fun observeSession() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.getSessionResult.collect {
                    when (it) {
                        ResultState.Loading -> false
                        is ResultState.Success -> {
                            currentUser = it.data
                            Glide.with(requireContext())
                                .load(currentUser?.photoUrl)
                                .placeholder(R.drawable.ic_person_32dp)
                                .error(R.drawable.ic_person_32dp)
                                .into(binding.civProfile)
                            binding.apply {
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
                        }

                        is ResultState.Error -> {
                            showToast(requireContext(), it.error)
                        }
                    }
                }
            }
        }
    }

    private fun logout() {
        currentUser = null
        authViewModel.logout()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToEditProfile() {
        val action = ProfileFragmentDirections.actionNavigationProfileToEditProfileFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
