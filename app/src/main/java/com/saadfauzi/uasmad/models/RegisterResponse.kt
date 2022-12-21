package com.saadfauzi.uasmad.models

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("data")
	val data: RegisterData? = null,

	@field:SerializedName("token_type")
	val tokenType: String? = null
)

data class RegisterData(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("photo")
	val photo: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)
