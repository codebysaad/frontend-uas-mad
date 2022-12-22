package com.saadfauzi.uasmad.ui.pegawai

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.helper.Event
import com.saadfauzi.uasmad.models.*
import com.saadfauzi.uasmad.rest.ApiConfig
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PegawaiViewModel (private val pref: CustomSettingPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMessage = MutableLiveData<Event<String?>>()
    val isMessage: LiveData<Event<String?>> = _isMessage

    private val _listJPegawai = MutableLiveData<ArrayList<DataPegawai>?>()
    val listPegawai: LiveData<ArrayList<DataPegawai>?> = _listJPegawai

    private val _resultAddPegawai = MutableLiveData<DataPegawai>()
    val resultAddPegawai: LiveData<DataPegawai> = _resultAddPegawai

    private val _resultUpdatePegawai = MutableLiveData<UpdateDeletePegawai>()
    val resultUpdatePegawai: LiveData<UpdateDeletePegawai> = _resultUpdatePegawai

    private val _deleteResult = MutableLiveData<UpdateDeletePegawai?>()
    val deleteResult: LiveData<UpdateDeletePegawai?> = _deleteResult

    private val _listJabatan = MutableLiveData<ArrayList<DataJabatan>?>()
    val listJabatan: LiveData<ArrayList<DataJabatan>?> = _listJabatan

    fun getAccessToken(): LiveData<String> {
        return pref.getAccessToken().asLiveData()
    }

    fun getAllPegawai(token: String){
        _isLoading.value = true
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().getAllPegawai(
            "Bearer $token",
        )
        client.enqueue(object : Callback<PegawaiResponse> {
            override fun onResponse(
                call: Call<PegawaiResponse>,
                response: Response<PegawaiResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        _listJPegawai.value = responseBody.data
                    }
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<PegawaiResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun addPegawai(token: String, idJabatan: RequestBody, namaLengkap: RequestBody, alamat: RequestBody, tmptLahir: RequestBody, tglLahir: RequestBody){
        _isLoading.value = true
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().addPegawai(
            "Bearer $token",
            idJabatan,
            namaLengkap,
            alamat,
            tmptLahir,
            tglLahir
        )
        client.enqueue(object : Callback<DataPegawai> {
            override fun onResponse(
                call: Call<DataPegawai>,
                response: Response<DataPegawai>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    _resultAddPegawai.value = responseBody!!
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<DataPegawai>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun updatePegawai(token: String, idBody: RequestBody, idJabatan: RequestBody, namaLengkap: RequestBody, alamat: RequestBody, tmptLahir: RequestBody, tglLahir: RequestBody, idParams: Int){
        _isLoading.value = true
        val methode = "PUT".toRequestBody()
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().updatePegawai(
            "Bearer $token",
            methode,
            idBody,
            idJabatan,
            namaLengkap,
            alamat,
            tmptLahir,
            tglLahir,
            idParams
        )
        client.enqueue(object : Callback<UpdateDeletePegawai> {
            override fun onResponse(
                call: Call<UpdateDeletePegawai>,
                response: Response<UpdateDeletePegawai>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    _resultUpdatePegawai.value = responseBody!!
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<UpdateDeletePegawai>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun deletePegawai(token: String, data: DataPegawai){
        _isLoading.value = true
        val idBody = data.id.toString().toRequestBody()
        val idParams = data.id
        val methode = "DELETE".toRequestBody()
        val client = ApiConfig.getApiService().deletePegawai(
            "Bearer $token",
            methode,
            idBody,
            idParams
        )
        client.enqueue(object : Callback<UpdateDeletePegawai> {
            override fun onResponse(
                call: Call<UpdateDeletePegawai>,
                response: Response<UpdateDeletePegawai>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _deleteResult.value = responseBody
                    }
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<UpdateDeletePegawai>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun getAllJabatan(token: String){
        _isLoading.value = true
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().getAllJabatan(
            "Bearer $token",
        )
        client.enqueue(object : Callback<JabatanResponse> {
            override fun onResponse(
                call: Call<JabatanResponse>,
                response: Response<JabatanResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        _listJabatan.value = responseBody.data
                    }
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<JabatanResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }
}