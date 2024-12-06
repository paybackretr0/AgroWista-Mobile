package com.example.agrowista.data.response

import com.google.gson.annotations.SerializedName

data class AccountResponse(

	@field:SerializedName("data")
	val data: DataUser? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataUser(

	@field:SerializedName("DataDiri")
	val dataDiri: DataDiriFull? = null,

	@field:SerializedName("total_point")
	val totalPoint: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class DataDiriFull(

	@field:SerializedName("asal")
	val asal: String? = null,

	@field:SerializedName("umur")
	val umur: Int? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("pekerjaan")
	val pekerjaan: String? = null,

	@field:SerializedName("jenis_kelamin")
	val jenisKelamin: String? = null
)
