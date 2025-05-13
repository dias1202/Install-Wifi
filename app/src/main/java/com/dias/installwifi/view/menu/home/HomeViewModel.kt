package com.dias.installwifi.view.menu.home

import androidx.lifecycle.ViewModel
import com.dias.installwifi.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

}