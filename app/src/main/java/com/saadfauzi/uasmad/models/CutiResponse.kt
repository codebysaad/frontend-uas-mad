package com.saadfauzi.uasmad.models

import com.google.gson.annotations.SerializedName

data class CutiResponse(

	@field:SerializedName("data")
	val data: ArrayList<Cuti>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Cuti(

	@field:SerializedName("tgl_akhir")
	val tglAkhir: String? = null,

	@field:SerializedName("tgl_status")
	val tglStatus: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("jns_cuti")
	val jnsCuti: String? = null,

	@field:SerializedName("tgl_awal")
	val tglAwal: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("alasan")
	val alasan: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
