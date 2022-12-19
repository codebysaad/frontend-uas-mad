package com.saadfauzi.uasmad.ui.jeniscuti

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

class JenisCutiViewModel(private val pref: CustomSettingPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMessage = MutableLiveData<Event<String?>>()
    val isMessage: LiveData<Event<String?>> = _isMessage

    private val _listJenisCuti = MutableLiveData<ArrayList<JenisCuti>?>()
    val listJenisCuti: LiveData<ArrayList<JenisCuti>?> = _listJenisCuti

    private val _resultAddJenisCuti = MutableLiveData<JenisCutiResponse>()
    val resultAddJenisCuti: LiveData<JenisCutiResponse> = _resultAddJenisCuti

    private val _resultUpdateJenisCuti = MutableLiveData<JenisCutiResponse>()
    val resultUpdateJenisCuti: LiveData<JenisCutiResponse> = _resultUpdateJenisCuti

    private val _deleteResult = MutableLiveData<JenisCutiResponse?>()
    val deleteResult: LiveData<JenisCutiResponse?> = _deleteResult

    fun getAccessToken(): LiveData<String> {
        return pref.getAccessToken().asLiveData()
    }

    fun getAllJenisCuti(token: String){
        _isLoading.value = true
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().getAllJenisCuti(
            "Bearer $token",
        )
        client.enqueue(object : Callback<JenisCutiResponse> {
            override fun onResponse(
                call: Call<JenisCutiResponse>,
                response: Response<JenisCutiResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        _listJenisCuti.value = responseBody.data
                    }
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<JenisCutiResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun addJenisCuti(token: String, jnsCuti: RequestBody, desc: RequestBody){
        _isLoading.value = true
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().addJenisCuti(
            "Bearer $token",
            jnsCuti,
            desc
        )
        client.enqueue(object : Callback<JenisCutiResponse> {
            override fun onResponse(
                call: Call<JenisCutiResponse>,
                response: Response<JenisCutiResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    _resultAddJenisCuti.value = responseBody!!
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<JenisCutiResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun updateJenisCuti(token: String,idBody: RequestBody, jnsCuti: RequestBody, desc: RequestBody, idParams: Int){
        _isLoading.value = true
        val methode = "PUT".toRequestBody()
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().updateJenisCuti(
            "Bearer $token",
            methode,
            idBody,
            jnsCuti,
            desc,
            idParams
        )
        client.enqueue(object : Callback<JenisCutiResponse> {
            override fun onResponse(
                call: Call<JenisCutiResponse>,
                response: Response<JenisCutiResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    _resultUpdateJenisCuti.value = responseBody!!
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<JenisCutiResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun deleteJenisCuti(token: String, data: JenisCuti){
        _isLoading.value = true
        val idBody = data.id.toString().toRequestBody()
        val idParams = data.id
        val methode = "DELETE".toRequestBody()
        val client = ApiConfig.getApiService().deleteJenisCuti(
            "Bearer $token",
            methode,
            idBody,
            idParams
        )
        client.enqueue(object : Callback<JenisCutiResponse> {
            override fun onResponse(
                call: Call<JenisCutiResponse>,
                response: Response<JenisCutiResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        _deleteResult.value = responseBody
                    }
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<JenisCutiResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }
}