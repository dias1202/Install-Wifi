package com.dias.installwifi.view.viewmodel

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
class DetailViewModel @Inject constructor(
    private val packageRepository: PackageRepository,
    private val bannerRepository: BannerRepository
) : ViewModel() {

    private val _getDetailPackageResult = MutableStateFlow<ResultState<Package>>(ResultState.Loading)
    val getDetailPackageResult: StateFlow<ResultState<Package>> = _getDetailPackageResult

    private val _getDetailBannerPromoResult = MutableStateFlow<ResultState<Banner>>(ResultState.Loading)
    val getDetailBannerPromoResult: StateFlow<ResultState<Banner>> = _getDetailBannerPromoResult

    fun getPackageById(packageId: Int) {
        viewModelScope.launch {
            packageRepository.getPackageById(packageId).collect {
                _getDetailPackageResult.value = it
            }
        }
    }

    fun getBannerPromoById(bannerId: Int) {
        viewModelScope.launch {
            bannerRepository.getBannerPromoById(bannerId).collect {
                _getDetailBannerPromoResult.value = it
            }
        }
    }

}