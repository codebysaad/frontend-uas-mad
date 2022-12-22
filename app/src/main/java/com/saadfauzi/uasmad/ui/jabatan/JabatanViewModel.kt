package com.saadfauzi.uasmad.ui.jabatan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.helper.Event
import com.saadfauzi.uasmad.models.DataJabatan
import com.saadfauzi.uasmad.models.JabatanResponse
import com.saadfauzi.uasmad.rest.ApiConfig
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JabatanViewModel(private val pref: CustomSettingPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMessage = MutableLiveData<Event<String?>>()
    val isMessage: LiveData<Event<String?>> = _isMessage

    private val _listJabatan = MutableLiveData<ArrayList<DataJabatan>?>()
    val listJabatan: LiveData<ArrayList<DataJabatan>?> = _listJabatan

    private val _resultAddJabatan = MutableLiveData<DataJabatan>()
    val resultAddJabatan: LiveData<DataJabatan> = _resultAddJabatan

    private val _resultUpdateJabatan = MutableLiveData<DataJabatan>()
    val resultUpdateJabatan: LiveData<DataJabatan> = _resultUpdateJabatan

    private val _deleteResult = MutableLiveData<JabatanResponse?>()
    val deleteResult: LiveData<JabatanResponse?> = _deleteResult

    fun getAccessToken(): LiveData<String> {
        return pref.getAccessToken().asLiveData()
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

    fun addJabatan(token: String, namaJabatan: RequestBody, tugas: RequestBody){
        _isLoading.value = true
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().addJabatan(
            "Bearer $token",
            namaJabatan,
            tugas
        )
        client.enqueue(object : Callback<DataJabatan> {
            override fun onResponse(
                call: Call<DataJabatan>,
                response: Response<DataJabatan>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    _resultAddJabatan.value = responseBody!!
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<DataJabatan>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun updateJabatan(token: String, idBody: RequestBody, namaJabatan: RequestBody, tugas: RequestBody, idParams: Int){
        _isLoading.value = true
        val methode = "PUT".toRequestBody()
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().updateJabatan(
            "Bearer $token",
            methode,
            idBody,
            namaJabatan,
            tugas,
            idParams
        )
        client.enqueue(object : Callback<DataJabatan> {
            override fun onResponse(
                call: Call<DataJabatan>,
                response: Response<DataJabatan>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    _resultUpdateJabatan.value = responseBody!!
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<DataJabatan>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun deleteJenisCuti(token: String, data: DataJabatan){
        _isLoading.value = true
        val idBody = data.id.toString().toRequestBody()
        val idParams = data.id
        val methode = "DELETE".toRequestBody()
        val client = ApiConfig.getApiService().deleteJabatan(
            "Bearer $token",
            methode,
            idBody,
            idParams
        )
        client.enqueue(object : Callback<JabatanResponse> {
            override fun onResponse(
                call: Call<JabatanResponse>,
                response: Response<JabatanResponse>
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

            override fun onFailure(call: Call<JabatanResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }
}