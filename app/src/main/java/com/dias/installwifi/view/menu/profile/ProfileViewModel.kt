package com.dias.installwifi.view.menu.profile

import androidx.lifecycle.ViewModel
import com.dias.installwifi.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

}