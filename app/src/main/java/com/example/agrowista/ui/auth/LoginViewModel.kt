package com.example.agrowista.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.data.response.AuthResponse
import com.example.agrowista.data.retro.ApiConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<AuthResponse>>()
    val loginResult: LiveData<Result<AuthResponse>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var loginJob: Job? = null

    fun login(email: String, password: String) {
        loginJob?.cancel()

        loginJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiConfig.api.login(email, password)
                if (!response.error!!) {
                    response.user?.let { user ->
                        user.id?.let { id ->
                            userPreferences.saveUser(id)
                            userPreferences.setLoggedInStatus(true)
                        }
                    }
                    _loginResult.postValue(Result.success(response))
                    _isLoading.value = false
                } else {
                    _loginResult.postValue(Result.failure(Exception(response.message)))
                    Log.d("LoginViewModel", "login: ${response.message}")
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    return@launch
                }
                _isLoading.value = false
                _loginResult.postValue(Result.failure(e))
                Log.e("LoginViewModel", "loginError: ${e.message}")
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

    override fun onCleared() {
        super.onCleared()
        loginJob?.cancel()
    }
}