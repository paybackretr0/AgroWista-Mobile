package com.example.agrowista.ui.report

import androidx.lifecycle.ViewModel
import com.example.agrowista.data.UserPreferences
import com.example.agrowista.data.response.AccountResponse
import com.example.agrowista.data.response.LaporanResponse
import com.example.agrowista.data.retro.ApiConfig
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ReportViewModel (private val userPreferences: UserPreferences): ViewModel() {
    private val apiService = ApiConfig.api

    suspend fun getUserId(): String {
        return userPreferences.getUserId().first()
    }

    suspend fun getUserProfile(userId: String): AccountResponse {
        return apiService.profile(userId)
    }

    suspend fun isUserLoggedIn(): Boolean {
        return userPreferences.isUserLoggedIn().first()
    }

    suspend fun sendReport(
        judul: String,
        deskripsi: String,
        imageFile: File
    ): LaporanResponse {
        val userId = getUserId().toInt().toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val juduBody = judul.toRequestBody("text/plain".toMediaTypeOrNull())
        val deskripsiBody = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = imageFile.let {
            MultipartBody.Part.createFormData(
                "gambar",
                it.name,
                it.asRequestBody("image/jpg".toMediaTypeOrNull())
            )
        }
        return apiService.sendReport(userId, juduBody, deskripsiBody, imagePart)
    }

    suspend fun sendReportGuest(
        nama: String?,
        umur: Int,
        asal: String,
        pekerjaan: String,
        jenisKelamin: String,
        judul: String,
        deskripsi: String,
        imageFile: File
    ): LaporanResponse {
        val namaBody = nama?.toRequestBody("text/plain".toMediaTypeOrNull())
        val umurBody = umur.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val asalBody = asal.toRequestBody("text/plain".toMediaTypeOrNull())
        val pekerjaanBody = pekerjaan.toRequestBody("text/plain".toMediaTypeOrNull())
        val jenisKelaminBody = jenisKelamin.toRequestBody("text/plain".toMediaTypeOrNull())
        val judulBody = judul.toRequestBody("text/plain".toMediaTypeOrNull())
        val deskripsiBody = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = imageFile.let {
            MultipartBody.Part.createFormData(
                "gambar",
                it.name,
                it.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        return apiService.sendReportGuest(
            namaBody,
            umurBody,
            asalBody,
            pekerjaanBody,
            jenisKelaminBody,
            judulBody,
            deskripsiBody,
            imagePart
        )
    }
}