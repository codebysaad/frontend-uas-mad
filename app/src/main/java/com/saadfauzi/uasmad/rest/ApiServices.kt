package com.saadfauzi.uasmad.rest

import com.saadfauzi.uasmad.helper.Helpers
import com.saadfauzi.uasmad.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {
    @FormUrlEncoded
    @POST(Helpers.ENDPOINT_LOGIN)
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST(Helpers.ENDPOINT_LOGOUT)
    fun logout(
        @Header("Authorization") token: String,
    ): Call<LogoutResponse>

    @Multipart
    @POST(Helpers.ENDPOINT_ATTENDANCE)
    fun addAttend(
        @Header("Authorization") token: String,
        @Part("type") type: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("long") long: RequestBody,
        @Part photo: MultipartBody.Part,
    ): Call<PostAttendanceResponse>

    @GET(Helpers.ENDPOINT_ATTENDANCE)
    fun getAllAttendance(
        @Header("Authorization") token: String,
    ): Call<ListAttendanceResponse>

    @DELETE("${Helpers.ENDPOINT_ATTENDANCE}/{attendance}")
    fun deleteAttendance(
        @Header("Authorization") token: String,
        @Path("attendance") id: Int?,
    ): Call<GeneralResponse>

    @GET(Helpers.ENDPOINT_JENIS_CUTI)
    fun getAllJenisCuti(
        @Header("Authorization") token: String,
    ): Call<JenisCutiResponse>

    @Multipart
    @POST(Helpers.ENDPOINT_JENIS_CUTI)
    fun addJenisCuti(
        @Header("Authorization") token: String,
        @Part("jenis_cuti") jenisCuti: RequestBody,
        @Part("deskripsi") description: RequestBody,
    ): Call<JenisCutiResponse>

    @Multipart
    @POST("${Helpers.ENDPOINT_JENIS_CUTI}/{id}")
    fun updateJenisCuti(
        @Header("Authorization") token: String,
        @Part("_method") methode: RequestBody,
        @Part("id") idBody: RequestBody,
        @Part("jenis_cuti") jenisCuti: RequestBody,
        @Part("deskripsi") description: RequestBody,
        @Path("id") idParams: Int,
    ): Call<JenisCutiResponse>

    @Multipart
    @POST("${Helpers.ENDPOINT_JENIS_CUTI}/{id}")
    fun deleteJenisCuti(
        @Header("Authorization") token: String,
        @Part("_method") methode: RequestBody,
        @Part("id") id: RequestBody,
        @Path("id") idParams: Int,
    ): Call<JenisCutiResponse>
}