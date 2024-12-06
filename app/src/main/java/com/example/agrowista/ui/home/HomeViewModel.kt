package com.example.agrowista.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.data.response.HomeResponse
import com.example.agrowista.data.retro.ApiConfig
import kotlinx.coroutines.launch

class HomeViewModel (private val userPreferences: UserPreferences) : ViewModel() {
    private val _homeData = MutableLiveData<HomeResponse>()
    val homeData: LiveData<HomeResponse> = _homeData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val apiService = ApiConfig.api

    fun fetchHomeData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.home()
                _homeData.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}