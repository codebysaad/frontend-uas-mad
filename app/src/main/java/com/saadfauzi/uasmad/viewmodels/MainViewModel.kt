package com.saadfauzi.uasmad.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.helper.Event
import com.saadfauzi.uasmad.models.LoginResponse
import com.saadfauzi.uasmad.models.LogoutResponse
import com.saadfauzi.uasmad.rest.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel (private val pref: CustomSettingPreferences): ViewModel() {

    private val TAG = "MainViewModel"

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMessage = MutableLiveData<Event<String?>>()
    val isMessage: LiveData<Event<String?>> = _isMessage

    private val _logoutResult = MutableLiveData<LogoutResponse?>()
    val logoutResult: LiveData<LogoutResponse?> = _logoutResult

    fun getAccessToken(): LiveData<String> {
        return pref.getAccessToken().asLiveData()
    }

    fun getUsername(): LiveData<String> {
        return pref.getUsername().asLiveData()
    }

    fun getEmail(): LiveData<String> {
        return pref.getEmail().asLiveData()
    }

    fun getPhoto(): LiveData<String> {
        return pref.getPhoto().asLiveData()
    }

    fun saveAccessToken(isLogged: Boolean, token: String, username: String, email: String, image: String) {
        viewModelScope.launch {
            pref.saveAccessToken(isLogged, token, username, email, image)
        }
    }

    fun logout(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().logout(token)
        client.enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if(responseBody.success == true) {
                            _logoutResult.value = responseBody
                            _isMessage.value = Event(responseBody.message)
//                            saveAccessToken(false, "No Auth")
                            Log.d(TAG, responseBody.message.toString())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
                Log.e(TAG, t.message.toString())
            }

        })
    }
}