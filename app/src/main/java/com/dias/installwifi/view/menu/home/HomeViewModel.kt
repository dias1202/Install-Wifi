package com.dias.installwifi.view.menu.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dias.installwifi.data.ResultState
import com.dias.installwifi.data.model.Banner
import com.dias.installwifi.data.model.Package
import com.dias.installwifi.data.repository.BannerRepository
import com.dias.installwifi.data.repository.PackageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val packageRepository: PackageRepository,
    private val bannerRepository: BannerRepository
) : ViewModel() {
    private val _getPackageResult = MutableStateFlow<ResultState<List<Package>>>(ResultState.Loading)
    val getPackageResult: StateFlow<ResultState<List<Package>>> = _getPackageResult

    private val _getBannerResult = MutableStateFlow<ResultState<List<Banner>>>(ResultState.Loading)
    val getBannerResult: StateFlow<ResultState<List<Banner>>> = _getBannerResult

    private val _getBannerPromoResult = MutableStateFlow<ResultState<List<Banner>>>(ResultState.Loading)
    val getBannerPromoResult: StateFlow<ResultState<List<Banner>>> = _getBannerPromoResult

    fun getPackages() {
        viewModelScope.launch {
            packageRepository.getPackages().collect {
                _getPackageResult.value = it
            }
        }
    }

    fun getBanners() {
        viewModelScope.launch {
            bannerRepository.getBanners().collect {
                _getBannerResult.value = it
            }
        }
    }

    fun getBannerPromo() {
        viewModelScope.launch {
            bannerRepository.getBannerPromo().collect {
                _getBannerPromoResult.value = it
            }
        }
    }
}