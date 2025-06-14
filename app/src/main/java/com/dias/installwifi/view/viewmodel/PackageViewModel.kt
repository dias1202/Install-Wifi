package com.dias.installwifi.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.Package
import com.dias.installwifi.data.repository.PackageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackageViewModel @Inject constructor(
    private val packageRepository: PackageRepository,
) : ViewModel() {

    private val _getPackageResult =
        MutableStateFlow<ResultState<List<Package>>>(ResultState.Loading)
    val getPackageResult: StateFlow<ResultState<List<Package>>> = _getPackageResult

    fun getPackages() {
        viewModelScope.launch {
            packageRepository.getPackages().collect {
                _getPackageResult.value = it
            }
        }
    }

}