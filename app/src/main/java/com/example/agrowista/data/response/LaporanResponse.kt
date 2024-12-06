package com.example.agrowista.data.response

import com.google.gson.annotations.SerializedName

data class LaporanResponse(

	@field:SerializedName("data")
	val data: DataLaporan? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataLaporan(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("judul")
	val judul: String? = null,

	@field:SerializedName("gambar")
	val gambar: String? = null,

	@field:SerializedName("id_data_diri")
	val idDataDiri: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
