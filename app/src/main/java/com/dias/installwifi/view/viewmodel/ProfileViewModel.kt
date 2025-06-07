package com.dias.installwifi.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.User
import com.dias.installwifi.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _updateProfileResult = MutableStateFlow<ResultState<User>>(ResultState.Loading)
    val updateProfileResult: StateFlow<ResultState<User>> = _updateProfileResult

    fun updateProfile(name: String, phoneNumber: String?, address: String?, file: File?) {
        viewModelScope.launch {
            userRepository.updateProfile(name, phoneNumber, address, file).collect { result ->
                _updateProfileResult.value = result
            }
        }
    }
}
