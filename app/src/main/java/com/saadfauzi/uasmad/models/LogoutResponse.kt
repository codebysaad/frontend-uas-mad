package com.saadfauzi.uasmad.models

import com.google.gson.annotations.SerializedName

data class LogoutResponse(

	@field:SerializedName("data")
	val data: Any? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
