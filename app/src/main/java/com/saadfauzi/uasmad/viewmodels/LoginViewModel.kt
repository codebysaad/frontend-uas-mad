package com.saadfauzi.uasmad.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.helper.Event
import com.saadfauzi.uasmad.models.LoginResponse
import com.saadfauzi.uasmad.rest.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel (private val pref: CustomSettingPreferences) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMessage = MutableLiveData<Event<String?>>()
    val isMessage: LiveData<Event<String?>> = _isMessage

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    fun getStateLogin(): LiveData<Boolean> {
        return pref.getLoginState().asLiveData()
    }

    fun saveAccessToken(isLogged: Boolean, token: String) {
        viewModelScope.launch {
            pref.saveAccessToken(isLogged, token)
        }
    }

    fun postLogin(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _loginResult.value = responseBody
                        _isMessage.value = Event(responseBody.message)
                        Log.d(TAG, responseBody.message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
                Log.e(TAG, t.message.toString())
            }

        })
    }
}