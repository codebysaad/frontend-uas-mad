package com.saadfauzi.uasmad.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class PegawaiResponse(

	@field:SerializedName("data")
	val data: ArrayList<DataPegawai>? = null,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String,
)

@Parcelize
data class DataPegawai(

	@field:SerializedName("tugas")
	val tugas: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("tmpt_lahir")
	val tmptLahir: String? = null,

	@field:SerializedName("nama_lengkap")
	val namaLengkap: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("tgl_lahir")
	val tglLahir: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("id_jabatan")
	val idJabatan: Int,

	@field:SerializedName("nama_jabatan")
	val namaJabatan: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null
): Parcelable

data class UpdateDeletePegawai(
	@field:SerializedName("data")
	val data: Int? = null,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String,
)