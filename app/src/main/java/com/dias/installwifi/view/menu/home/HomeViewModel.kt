package com.dias.installwifi.view.menu.home

import androidx.lifecycle.ViewModel
import com.dias.installwifi.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

}