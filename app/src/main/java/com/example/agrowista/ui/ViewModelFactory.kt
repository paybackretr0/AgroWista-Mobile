package com.example.agrowista.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.ui.auth.LoginViewModel
import com.example.agrowista.ui.account.AccountViewModel
import com.example.agrowista.ui.home.HomeViewModel
import com.example.agrowista.ui.regis.RegisterViewModel
import com.example.agrowista.ui.report.ReportViewModel
import com.example.agrowista.ui.ulasan.UlasanViewModel

class ViewModelFactory(private val userPreferences: UserPreferences) : ViewModelProvider
.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userPreferences) as T
            }
            modelClass.isAssignableFrom(AccountViewModel::class.java) -> {
                AccountViewModel(userPreferences) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userPreferences) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel() as T
            }
            modelClass.isAssignableFrom(UlasanViewModel::class.java) -> {
                UlasanViewModel(userPreferences) as T
            }
            modelClass.isAssignableFrom(ReportViewModel::class.java) -> {
                ReportViewModel(userPreferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}