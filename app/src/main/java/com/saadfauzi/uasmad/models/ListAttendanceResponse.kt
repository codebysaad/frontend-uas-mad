package com.saadfauzi.uasmad.models

import com.google.gson.annotations.SerializedName

data class ListAttendanceResponse(

	@field:SerializedName("data")
	val data: ArrayList<ListAttendance>,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListAttendance(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("attend")
	val attend: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("photo")
	val photo: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("long")
	val jsonMemberLong: String? = null,

	@field:SerializedName("lat")
	val lat: String? = null
)
