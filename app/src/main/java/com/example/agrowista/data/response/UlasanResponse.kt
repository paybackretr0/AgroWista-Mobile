package com.example.agrowista.data.response

import com.google.gson.annotations.SerializedName

data class UlasanResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("ulasan")
	val ulasan: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("jenisWisataId")
	val jenisWisataId: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("id_data_diri")
	val idDataDiri: Any? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
