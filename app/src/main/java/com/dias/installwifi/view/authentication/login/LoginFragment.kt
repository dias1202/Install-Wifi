package com.dias.installwifi.view.authentication.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dias.installwifi.MainActivity
import com.dias.installwifi.R
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.databinding.FragmentLoginBinding
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import com.dias.installwifi.view.viewmodel.AuthViewModel
import com.dias.installwifi.view.authentication.ForgotPasswordActivity
import com.dias.installwifi.view.authentication.register.RegisterFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var loading: View

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    authViewModel.loginWithGoogle(idToken)
                    observeGoogleLoginResult()
                } else {
                    showToast(requireContext(), "Google ID token is null")
                }
            } catch (e: ApiException) {
                showToast(requireContext(), "Google Sign-In failed: ${e.message}")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        loading = binding.loading

        binding.apply {
            tvSignUpClick.setOnClickListener {
                navigateToRegister()
            }

            loginButton.setOnClickListener {
                val email = binding.edInputEmail.text.toString()
                val password = edInputPassword.text.toString()
                authViewModel.login(email, password)
                observeLoginResult()
            }

            googleLogin.setOnClickListener {
                googleSignInClient.signOut().addOnCompleteListener {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                }
            }

            tvForgotPassword.setOnClickListener {
                navigateToForgotPassword()
            }
        }

        return view
    }

    private fun observeLoginResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loginResult.collect { result ->
                    when (result) {
                        ResultState.Loading -> showLoading(loading, true)
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            showToast(requireContext(), getString(R.string.login_email_successful))
                            authViewModel.saveSession(result.data)
                            navigateToHome()
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

    private fun observeGoogleLoginResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.googleLoginResult.collect { result ->
                    when (result) {
                        ResultState.Loading -> showLoading(loading, true)
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            showToast(requireContext(), getString(R.string.login_google_successful))
                            authViewModel.saveSession(result.data)
                            navigateToHome()
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

    private fun navigateToHome() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToRegister() {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                RegisterFragment(),
                RegisterFragment::class.java.simpleName
            )
            .commit()
    }

    private fun navigateToForgotPassword() {
        val intent = Intent(requireContext(), ForgotPasswordActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}