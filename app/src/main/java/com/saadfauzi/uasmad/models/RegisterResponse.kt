package com.saadfauzi.uasmad.models

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("access_token")
	val accessToken: String,

	@field:SerializedName("data")
	val data: RegisterData,

	@field:SerializedName("token_type")
	val tokenType: String,
)

data class RegisterData(

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("photo")
	val photo: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("phone_number")
	val phoneNumber: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("email")
	val email: String
)
