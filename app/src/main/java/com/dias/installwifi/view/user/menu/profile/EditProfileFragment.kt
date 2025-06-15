package com.dias.installwifi.view.user.menu.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.dias.installwifi.R
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.User
import com.dias.installwifi.databinding.FragmentEditProfileBinding
import com.dias.installwifi.utils.PermissionHelper.hasPermissions
import com.dias.installwifi.utils.PermissionHelper.requestPermissionsIfNeeded
import com.dias.installwifi.utils.States.showLoading
import com.dias.installwifi.utils.States.showToast
import com.dias.installwifi.utils.uriToFile
import com.dias.installwifi.view.viewmodel.AuthViewModel
import com.dias.installwifi.view.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    private val loading by lazy { binding.loading }

    private var currentUser: User? = null
    private var selectedImageFile: File? = null

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private var isTechnician = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions.all { it.value }) {
                    openImagePicker()
                } else {
                    showToast(requireContext(), "Izin akses gambar ditolak")
                }
            }

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                showLoading(loading, false)
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri = result.data?.data
                    imageUri?.let {
                        binding.civEditProfile.setImageURI(it)
                        selectedImageFile = uriToFile(it, requireContext())
                    }
                }
            }

        authViewModel.getSession()

        observeSession()
        observeUpdateProfileResult()

        binding.apply {
            civEditProfile.setOnClickListener {
                if (hasPermissions(requireContext())) {
                    openImagePicker()
                } else {
                    requestPermissionsIfNeeded(requireContext(), permissionLauncher)
                }
            }
            btnEditSave.setOnClickListener { onSaveClicked() }
            tfAddress.setEndIconOnClickListener {
                // todo: add address picker
            }
        }

        return binding.root
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun onSaveClicked() {
        val name = binding.etEditName.text.toString().trim()
        val phone = binding.etEditPhone.text.toString().trim().ifEmpty { null }
        val address = binding.etEditAddress.text.toString().trim().ifEmpty { null }
        profileViewModel.updateProfile(name, phone, address, selectedImageFile)
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
                                .into(binding.civEditProfile)

                            binding.apply {
                                etEditName.setText(currentUser?.name)
                                etEditEmail.setText(currentUser?.email)
                                if (!currentUser?.phoneNumber.isNullOrBlank()) {
                                    etEditPhone.setText(currentUser?.phoneNumber)
                                } else {
                                    etEditPhone.text = null
                                }
                                if (!currentUser?.address.isNullOrBlank()) {
                                    etEditAddress.setText(currentUser?.address)
                                } else {
                                    etEditAddress.text = null
                                }
                            }

                            isTechnician = currentUser?.isTechnician == true
                        }

                        is ResultState.Error -> {
                            showToast(requireContext(), it.error)
                        }
                    }
                }
            }
        }
    }

    private fun observeUpdateProfileResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.updateProfileResult.collect {
                    when (it) {
                        ResultState.Loading -> showLoading(loading, true)
                        is ResultState.Success -> {
                            showLoading(loading, false)
                            authViewModel.saveSession(it.data, isTechnician)
                            showToast(requireContext(), "Profile updated successfully")
                            requireActivity().onBackPressedDispatcher.onBackPressed()
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

    override fun onResume() {
        super.onResume()
        showLoading(loading, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
