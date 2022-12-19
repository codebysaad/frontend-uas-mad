package com.saadfauzi.uasmad.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class JenisCutiResponse(

	@field:SerializedName("data")
	val data: ArrayList<JenisCuti>? = null,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

@Parcelize
data class JenisCuti(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("jenis_cuti")
	val jenisCuti: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("id")
	val id: Int
): Parcelable
