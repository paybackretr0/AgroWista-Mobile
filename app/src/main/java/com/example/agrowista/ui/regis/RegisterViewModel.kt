package com.example.agrowista.ui.regis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrowista.data.retro.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel () : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    val apiService = ApiConfig.api

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        age: String,
        gender: String,
        occupation: String,
        origin: String
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                when {
                    name.isEmpty() -> throw IllegalArgumentException("Nama tidak boleh kosong")
                    email.isEmpty() -> throw IllegalArgumentException("Email tidak boleh kosong")
                    password.isEmpty() -> throw IllegalArgumentException("Password tidak boleh kosong")
                    confirmPassword.isEmpty() -> throw IllegalArgumentException("Konfirmasi password tidak boleh kosong")
                    password != confirmPassword -> throw IllegalArgumentException("Password dan konfirmasi password tidak cocok")
                    age.isEmpty() -> throw IllegalArgumentException("Umur tidak boleh kosong")
                    gender.isEmpty() -> throw IllegalArgumentException("Jenis kelamin tidak boleh kosong")
                }

                val ageInt = age.toIntOrNull() ?: throw IllegalArgumentException("Umur harus berupa angka")
                if (ageInt <= 0) throw IllegalArgumentException("Umur tidak valid")

                val genderCode = when (gender) {
                    "Laki-Laki" -> "L"
                    "Perempuan" -> "P"
                    else -> throw IllegalArgumentException("Jenis kelamin tidak valid")
                }

                val response = apiService.register(
                    name = name,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    age = ageInt,
                    gender = genderCode,
                    occupation = occupation,
                    origin = origin
                )

                if (response.error == true) {
                    _errorMessage.value = response.message ?: "Terjadi kesalahan"
                } else {
                    _registerSuccess.value = true
                }
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    400 -> "Email sudah terdaftar atau data tidak valid"
                    500 -> "Terjadi kesalahan pada server"
                    else -> "Terjadi kesalahan: ${e.message()}"
                }
                _errorMessage.value = errorMessage
            } catch (e: IllegalArgumentException) {
                _errorMessage.value = e.message
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}