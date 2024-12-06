package com.example.agrowista.ui.account

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.data.response.AccountResponse
import com.example.agrowista.data.retro.ApiConfig
import kotlinx.coroutines.launch

class AccountViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    private val _accountData = MutableLiveData<AccountResponse>()
    val accountData: LiveData<AccountResponse> = _accountData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val apiService = ApiConfig.api

    fun getUserId(): LiveData<String> {
        return userPreferences.getUserId().asLiveData()
    }

    fun getAccountData(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.profile(id)
                _accountData.value = response
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        _isLoading.value = true
        viewModelScope.launch {
            userPreferences.logout()
            _isLoading.value = false
        }
    }
}