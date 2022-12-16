package com.saadfauzi.uasmad.rest

import com.saadfauzi.uasmad.helper.Helpers
import com.saadfauzi.uasmad.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {
//    @GET(Helpers.ADD_AND_GET_STORIES)
//    suspend fun getAllStories(
//        @Header("Authorization") token: String,
//        @Query("page") page: Int,
//        @Query("size") size: Int,
//    ): GetAllStoriesResponse

//    @FormUrlEncoded
//    @POST(Constanta.ENDPOINT_Re)
//    fun registerNewUser(
//        @Field("name") name: String,
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): Call<GeneralResponse>

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

//    @GET(Helpers.ADD_AND_GET_STORIES)
//    fun getStoriesInMap(
//        @Header("Authorization") token: String,
//        @Query("location") location: String,
//    ): Call<GetAllStoriesResponse>
}