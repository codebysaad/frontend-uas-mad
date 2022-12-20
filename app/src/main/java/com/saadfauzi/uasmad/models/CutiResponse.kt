package com.saadfauzi.uasmad.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class CutiResponse(

	@field:SerializedName("data")
	val data: ArrayList<Cuti>? = null,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

@Parcelize
data class Cuti(

	@field:SerializedName("tgl_akhir")
	val tglAkhir: String? = null,

	@field:SerializedName("tgl_status")
	val tglStatus: String? = null,

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
	val id: Int,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("alasan")
	val alasan: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
): Parcelable

data class UpdateDeleteCuti(
	@field:SerializedName("data")
	val data: Int? = null,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String,
)