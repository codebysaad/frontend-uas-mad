package com.saadfauzi.uasmad.models

import com.google.gson.annotations.SerializedName

data class GeneralResponse(
    @field:SerializedName("data")
    val data: PostData? = null,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String,
)
