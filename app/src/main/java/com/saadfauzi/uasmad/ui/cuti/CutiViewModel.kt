package com.saadfauzi.uasmad.ui.cuti

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.helper.Event
import com.saadfauzi.uasmad.models.*
import com.saadfauzi.uasmad.models.JenisCuti
import com.saadfauzi.uasmad.rest.ApiConfig
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CutiViewModel (private val pref: CustomSettingPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMessage = MutableLiveData<Event<String?>>()
    val isMessage: LiveData<Event<String?>> = _isMessage

    private val _listCuti = MutableLiveData<ArrayList<Cuti>?>()
    val listCuti: LiveData<ArrayList<Cuti>?> = _listCuti

    private val _listJenisCuti = MutableLiveData<ArrayList<JenisCuti>?>()
    val listJenisCuti: LiveData<ArrayList<JenisCuti>?> = _listJenisCuti

    private val _resultAddCuti = MutableLiveData<Cuti>()
    val resultAddCuti: LiveData<Cuti> = _resultAddCuti

    private val _resultUpdateCuti = MutableLiveData<UpdateDeleteCuti>()
    val resultUpdateCuti: LiveData<UpdateDeleteCuti> = _resultUpdateCuti

    private val _deleteResult = MutableLiveData<UpdateDeleteCuti?>()
    val deleteResult: LiveData<UpdateDeleteCuti?> = _deleteResult

    fun getAccessToken(): LiveData<String> {
        return pref.getAccessToken().asLiveData()
    }

    fun getAllCuti(token: String){
        _isLoading.value = true
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().getAllCuti(
            "Bearer $token",
        )
        client.enqueue(object : Callback<CutiResponse> {
            override fun onResponse(
                call: Call<CutiResponse>,
                response: Response<CutiResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        _listCuti.value = responseBody.data
                    }
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<CutiResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun addCuti(token: String, alasan: RequestBody, jnsCuti: RequestBody, tglAwal: RequestBody, tglAkhir: RequestBody){
        _isLoading.value = true
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().addCuti(
            "Bearer $token",
            alasan,
            jnsCuti,
            tglAwal,
            tglAkhir
        )
        client.enqueue(object : Callback<Cuti> {
            override fun onResponse(
                call: Call<Cuti>,
                response: Response<Cuti>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    _resultAddCuti.value = responseBody!!
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<Cuti>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun updateCuti(token: String, idBody: RequestBody, jnsCuti: RequestBody, alasan: RequestBody, tglAwal: RequestBody, tglAkhir: RequestBody, status: RequestBody, idParams: Int){
        _isLoading.value = true
        val methode = "PUT".toRequestBody()
        Log.i("TokenJenisCuti", token)
        val client = ApiConfig.getApiService().updateCuti(
            "Bearer $token",
            methode,
            idBody,
            jnsCuti,
            alasan,
            tglAwal,
            tglAkhir,
            status,
            idParams
        )
        client.enqueue(object : Callback<UpdateDeleteCuti> {
            override fun onResponse(
                call: Call<UpdateDeleteCuti>,
                response: Response<UpdateDeleteCuti>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    _resultUpdateCuti.value = responseBody!!
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<UpdateDeleteCuti>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun deleteCuti(token: String, data: Cuti){
        _isLoading.value = true
        val idBody = data.id.toString().toRequestBody()
        val idParams = data.id
        Log.i("Data.Id", data.id.toString())
        val methode = "DELETE".toRequestBody()
        val client = ApiConfig.getApiService().deleteCuti(
            "Bearer $token",
            methode,
            idBody,
            idParams
        )
        client.enqueue(object : Callback<UpdateDeleteCuti> {
            override fun onResponse(
                call: Call<UpdateDeleteCuti>,
                response: Response<UpdateDeleteCuti>
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

            override fun onFailure(call: Call<UpdateDeleteCuti>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
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
}