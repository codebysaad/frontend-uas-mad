package com.saadfauzi.uasmad.ui.listpresence

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.helper.Event
import com.saadfauzi.uasmad.models.GeneralResponse
import com.saadfauzi.uasmad.models.ListAttendance
import com.saadfauzi.uasmad.models.ListAttendanceResponse
import com.saadfauzi.uasmad.models.PostAttendanceResponse
import com.saadfauzi.uasmad.rest.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListPresenceViewModel(private val pref: CustomSettingPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMessage = MutableLiveData<Event<String?>>()
    val isMessage: LiveData<Event<String?>> = _isMessage

    private val _listAttendance = MutableLiveData<ArrayList<ListAttendance>>()
    val listAttendance: LiveData<ArrayList<ListAttendance>> = _listAttendance

    private val _deleteResult = MutableLiveData<GeneralResponse?>()
    val deleteResult: LiveData<GeneralResponse?> = _deleteResult

    fun getAccessToken(): LiveData<String> {
        return pref.getAccessToken().asLiveData()
    }

    fun getAllAttend(token: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getAllAttendance(
            "Bearer $token",
        )
        client.enqueue(object : Callback<ListAttendanceResponse> {
            override fun onResponse(
                call: Call<ListAttendanceResponse>,
                response: Response<ListAttendanceResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success == true) {
                        _listAttendance.value = responseBody.data
                    }
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<ListAttendanceResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun deleteAttend(token: String, data: ListAttendance){
        _isLoading.value = true
        val client = ApiConfig.getApiService().deleteAttendance(
            "Bearer $token",
            data.id
        )
        client.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
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

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }
}