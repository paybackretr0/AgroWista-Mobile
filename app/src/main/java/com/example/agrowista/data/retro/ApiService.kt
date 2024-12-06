package com.example.agrowista.data.retro

import com.example.agrowista.data.response.AccountResponse
import com.example.agrowista.data.response.AuthResponse
import com.example.agrowista.data.response.HomeResponse
import com.example.agrowista.data.response.LaporanResponse
import com.example.agrowista.data.response.UlasanResponse
import com.example.agrowista.data.response.WisataResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): AuthResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirmPassword") confirmPassword: String,
        @Field("age") age: Int,
        @Field("gender") gender: String,
        @Field("occupation") occupation: String,
        @Field("origin") origin: String,
    ): AuthResponse

    @GET("home")
    suspend fun home(): HomeResponse

    @GET("profile/{id}")
    suspend fun profile(@Path("id") id: String): AccountResponse

    @GET("jenis-wisata")
    suspend fun jenisWisata(): WisataResponse

    @FormUrlEncoded
    @POST("kirim-ulasan")
    suspend fun sendReview(
        @Field("userId") userId: Int,
        @Field("jenis_wisata") jenis_wisata: Int,
        @Field("ulasan") ulasan: String
    ): UlasanResponse

    @FormUrlEncoded
    @POST("kirim-ulasan/guest")
    suspend fun sendReviewGuest(
        @Field("nama") nama: String?,
        @Field("umur") umur: Int,
        @Field("asal") asal: String,
        @Field("pekerjaan") pekerjaan: String,
        @Field("jenis_kelamin") jenis_kelamin: String,
        @Field("jenis_wisata") jenis_wisata: Int,
        @Field("ulasan") ulasan: String
    ): UlasanResponse

    @Multipart
    @POST("kirim-laporan")
    suspend fun sendReport(
        @Part("userId") userId: RequestBody,
        @Part("judul") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part gambar: MultipartBody.Part
    ): LaporanResponse

    @Multipart
    @POST("kirim-laporan/guest")
    suspend fun sendReportGuest(
        @Part("nama") nama: RequestBody?,
        @Part("umur") umur: RequestBody,
        @Part("asal") asal: RequestBody,
        @Part("pekerjaan") pekerjaan: RequestBody,
        @Part("jenis_kelamin") jenis_kelamin: RequestBody,
        @Part("judul") judul: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part gambar: MultipartBody.Part
    ): LaporanResponse
}