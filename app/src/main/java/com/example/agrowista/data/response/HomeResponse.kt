package com.example.agrowista.data.response

import com.google.gson.annotations.SerializedName

data class HomeResponse(

	@field:SerializedName("data")
	val data: DataHome? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataHome(

	@field:SerializedName("ulasan")
	val ulasan: List<UlasanItem?>? = null,

	@field:SerializedName("total_ulasan")
	val totalUlasan: Int? = null,

	@field:SerializedName("total_wisata")
	val totalWisata: Int? = null,

	@field:SerializedName("total_produk")
	val totalProduk: Int? = null,

	@field:SerializedName("agro")
	val agro: List<AgroItem?>? = null
)

data class AgroItem(

	@field:SerializedName("nama_wisata")
	val namaWisata: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("gambar")
	val gambar: String? = null
)

data class UlasanItem(

	@field:SerializedName("ulasan")
	val ulasan: String? = null,

	@field:SerializedName("id_data_diri")
	val idDataDiri: Int? = null,

	@field:SerializedName("DataDiri")
	val dataDiri: DataDiri? = null,

)

data class DataDiri(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("pekerjaan")
	val pekerjaan: String? = null
)


