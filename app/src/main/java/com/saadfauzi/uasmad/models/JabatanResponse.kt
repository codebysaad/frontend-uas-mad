package com.saadfauzi.uasmad.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class JabatanResponse(

	@field:SerializedName("data")
	val data: ArrayList<DataJabatan>? = null,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String,
)

@Parcelize
data class DataJabatan(

	@field:SerializedName("tugas")
	val tugas: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("nama_jabatan")
	val namaJabatan: String? = null
): Parcelable
