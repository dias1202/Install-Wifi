package com.dias.installwifi.view.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.User
import com.dias.installwifi.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registerResult = MutableStateFlow<ResultState<User>>(ResultState.Loading)
    val registerResult: StateFlow<ResultState<User>> = _registerResult

    private val _loginResult = MutableStateFlow<ResultState<User>>(ResultState.Loading)
    val loginResult: StateFlow<ResultState<User>> = _loginResult

    private val _googleLoginResult = MutableStateFlow<ResultState<User>>(ResultState.Loading)
    val googleLoginResult: StateFlow<ResultState<User>> = _googleLoginResult


    private val _saveSessionResult = MutableStateFlow<ResultState<Boolean>>(ResultState.Loading)
    val saveSessionResult: StateFlow<ResultState<Boolean>> = _saveSessionResult

    private val _getSessionResult = MutableStateFlow<ResultState<User>>(ResultState.Loading)
    val getSessionResult: StateFlow<ResultState<User>> = _getSessionResult

    private val _logoutResult = MutableStateFlow<ResultState<Boolean>>(ResultState.Loading)
    val logoutResult: StateFlow<ResultState<Boolean>> = _logoutResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            userRepository.register(name, email, password).collect {
                _registerResult.value = it
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            userRepository.login(email, password).collect {
                _loginResult.value = it
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            userRepository.loginWithGoogle(idToken).collect {
                _googleLoginResult.value = it
            }
        }
    }

    fun saveSession(user: User) {
        viewModelScope.launch {
            userRepository.saveSession(user).collect {
                _saveSessionResult.value = it
            }
        }
    }

    fun getSession() {
        viewModelScope.launch {
            userRepository.getSession().collect {
                _getSessionResult.value = it
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout().collect {
                _logoutResult.value = it
            }
        }
    }
}