package com.saadfauzi.uasmad.rest

import com.saadfauzi.uasmad.helper.Helpers
import com.saadfauzi.uasmad.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
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
    @POST(Helpers.ENDPOINT_Register)
    fun register(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("password") password: RequestBody,
        @Part("role") role: RequestBody,
        @Part photo: MultipartBody.Part,
    ): Call<RegisterResponse>

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
    ): Call<JenisCuti>

    @Multipart
    @POST("${Helpers.ENDPOINT_JENIS_CUTI}/{id}")
    fun updateJenisCuti(
        @Header("Authorization") token: String,
        @Part("_method") methode: RequestBody,
        @Part("id") idBody: RequestBody,
        @Part("jenis_cuti") jenisCuti: RequestBody,
        @Part("deskripsi") description: RequestBody,
        @Path("id") idParams: Int,
    ): Call<JenisCuti>

    @Multipart
    @POST("${Helpers.ENDPOINT_JENIS_CUTI}/{id}")
    fun deleteJenisCuti(
        @Header("Authorization") token: String,
        @Part("_method") methode: RequestBody,
        @Part("id") id: RequestBody,
        @Path("id") idParams: Int,
    ): Call<JenisCutiResponse>

    @GET(Helpers.ENDPOINT_CUTI)
    fun getAllCuti(
        @Header("Authorization") token: String,
    ): Call<CutiResponse>

    @Multipart
    @POST(Helpers.ENDPOINT_CUTI)
    fun addCuti(
        @Header("Authorization") token: String,
        @Part("alasan") alasan: RequestBody,
        @Part("jns_cuti") jnsCuti: RequestBody,
        @Part("tgl_awal") tglAwal: RequestBody,
        @Part("tgl_akhir") tglAkhir: RequestBody,
    ): Call<Cuti>

    @Multipart
    @POST("${Helpers.ENDPOINT_CUTI}/{id}")
    fun updateCuti(
        @Header("Authorization") token: String,
        @Part("_method") methode: RequestBody,
        @Part("id") idBody: RequestBody,
        @Part("jns_cuti") jenisCuti: RequestBody,
        @Part("alasan") alasan: RequestBody,
        @Part("tgl_awal") tglAwal: RequestBody,
        @Part("tgl_akhir") tglAkhir: RequestBody,
        @Part("status") status: RequestBody,
        @Path("id") idParams: Int,
    ): Call<UpdateDeleteCuti>

    @Multipart
    @POST("${Helpers.ENDPOINT_CUTI}/{id}")
    fun deleteCuti(
        @Header("Authorization") token: String,
        @Part("_method") methode: RequestBody,
        @Part("id") id: RequestBody,
        @Path("id") idParams: Int,
    ): Call<UpdateDeleteCuti>

    @GET(Helpers.ENDPOINT_PEGAWAI)
    fun getAllPegawai(
        @Header("Authorization") token: String,
    ): Call<PegawaiResponse>

    @Multipart
    @POST(Helpers.ENDPOINT_PEGAWAI)
    fun addPegawai(
        @Header("Authorization") token: String,
        @Part("nama_lengkap") namaLengkap: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("tmpt_lahir") tmptLahir: RequestBody,
        @Part("tgl_lahir") tglLahir: RequestBody,
    ): Call<DataPegawai>

    @Multipart
    @POST("${Helpers.ENDPOINT_PEGAWAI}/{id}")
    fun updatePegawai(
        @Header("Authorization") token: String,
        @Part("_method") methode: RequestBody,
        @Part("id") idBody: RequestBody,
        @Part("nama_lengkap") namaLengkap: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("tmpt_lahir") tmptLahir: RequestBody,
        @Part("tgl_lahir") tglLahir: RequestBody,
        @Path("id") idParams: Int,
    ): Call<UpdateDeletePegawai>

    @Multipart
    @POST("${Helpers.ENDPOINT_PEGAWAI}/{id}")
    fun deletePegawai(
        @Header("Authorization") token: String,
        @Part("_method") methode: RequestBody,
        @Part("id") id: RequestBody,
        @Path("id") idParams: Int,
    ): Call<UpdateDeletePegawai>
}