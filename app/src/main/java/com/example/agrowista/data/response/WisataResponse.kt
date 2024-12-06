package com.example.agrowista.data.response

import com.google.gson.annotations.SerializedName

data class WisataResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataItem(

	@field:SerializedName("nama_wisata")
	val namaWisata: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
