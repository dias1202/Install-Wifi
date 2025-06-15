package com.dias.installwifi.view.authentication.register

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
import com.dias.installwifi.databinding.FragmentRegisterBinding
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import com.dias.installwifi.view.viewmodel.AuthViewModel
import com.dias.installwifi.view.authentication.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var loading: View

    private var isTechnician = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        loading = binding.loading

        binding.apply {
            backButton.setOnClickListener {
                navigateToLogin()
            }
            tvSignInClick.setOnClickListener {
                navigateToLogin()
            }
            registerButton.setOnClickListener {
                val name = binding.edInputFullName.text.toString()
                val email = binding.edInputEmail.text.toString()
                val password = edInputSetPassword.text.toString()
                isTechnician = cbRegisterAsTechnician.isChecked
                authViewModel.register(name, email, password, isTechnician)
                observeRegisterResult()
            }
        }
        return view
    }

    private fun observeRegisterResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.registerResult.collect { result ->
                    when (result) {
                        is ResultState.Loading -> showLoading(loading, true)
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            showToast(requireContext(), getString(R.string.registration_successful))
                            navigateToLogin()
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

    private fun navigateToLogin() {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                LoginFragment(),
                LoginFragment::class.java.simpleName
            )
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}