package com.saadfauzi.uasmad.models

import com.google.gson.annotations.SerializedName

data class GetCutiResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataItem(

	@field:SerializedName("tgl_akhir")
	val tglAkhir: String? = null,

	@field:SerializedName("tgl_status")
	val tglStatus: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("jenis_cuti")
	val jenisCuti: String? = null,

	@field:SerializedName("jns_cuti")
	val jnsCuti: Int? = null,

	@field:SerializedName("tgl_awal")
	val tglAwal: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("alasan")
	val alasan: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
