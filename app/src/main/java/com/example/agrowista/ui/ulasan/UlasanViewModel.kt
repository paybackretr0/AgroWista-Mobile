package com.example.agrowista.ui.ulasan

import androidx.lifecycle.ViewModel
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.data.response.AccountResponse
import com.example.agrowista.data.response.UlasanResponse
import com.example.agrowista.data.response.WisataResponse
import com.example.agrowista.data.retro.ApiConfig
import kotlinx.coroutines.flow.first

class UlasanViewModel (private val userPreferences: UserPreferences): ViewModel() {

    private val apiService = ApiConfig.api

    suspend fun getUserId(): String {
        return userPreferences.getUserId().first()
    }

    suspend fun getUserProfile(userId: String): AccountResponse {
        return apiService.profile(userId)
    }

    suspend fun getJenisWisata(): WisataResponse {
        return apiService.jenisWisata()
    }

    suspend fun sendReview(jenisWisata: Int, ulasan: String): UlasanResponse {
        val userId = getUserId().toInt()
        return apiService.sendReview(userId, jenisWisata, ulasan)
    }

    suspend fun sendReviewGuest(nama: String?, umur: Int, asal: String, pekerjaan: String, jenis_kelamin: String, jenisWisata: Int, ulasan: String): UlasanResponse {
        return apiService.sendReviewGuest(nama, umur, asal, pekerjaan, jenis_kelamin, jenisWisata, ulasan)
    }
}