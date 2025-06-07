package com.dias.installwifi.view.menu.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        authViewModel.getSession()

        observeSession()
        observeUpdateProfileResult()

        binding.apply {
            civEditProfile.setOnClickListener { openImagePicker() }
            btnEditSave.setOnClickListener { onSaveClicked() }
            tfAddress.setEndIconOnClickListener {
            // todo: add address picker
            }
        }

        return binding.root
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissions.any {
                requireContext().checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
            }) {
            requestPermissions(permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openImagePicker()
            } else {
                showToast(requireContext(), "Permission denied to access images")
            }
        }
    }

    private fun openImagePicker() {
        if (!checkAndRequestPermissions()) return
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST) {
            showLoading(loading, false)
            if (resultCode == Activity.RESULT_OK && data != null) {
                val imageUri: Uri? = data.data
                imageUri?.let {
                    binding.civEditProfile.setImageURI(it)
                    selectedImageFile = uriToFile(it, requireContext())
                }
            }
        }
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
                            authViewModel.saveSession(it.data)
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

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val PERMISSION_REQUEST_CODE = 100

    }
}
