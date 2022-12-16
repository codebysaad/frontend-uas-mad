package com.saadfauzi.uasmad.ui.presence

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.helper.Event
import com.saadfauzi.uasmad.helper.GeoCoderUtil
import com.saadfauzi.uasmad.helper.LoadDataCallback
import com.saadfauzi.uasmad.models.LocationModel
import com.saadfauzi.uasmad.models.PostAttendanceResponse
import com.saadfauzi.uasmad.rest.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PresenceViewModel(private val pref: CustomSettingPreferences) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMessage = MutableLiveData<Event<String?>>()
    val isMessage: LiveData<Event<String?>> = _isMessage

    private val _latitude = MutableLiveData<String>()
    val latitude: LiveData<String> = _latitude

    private val _longitude = MutableLiveData<String>()
    val longitude: LiveData<String> = _longitude

    private val _attendResult = MutableLiveData<PostAttendanceResponse?>()
    val attendResult: LiveData<PostAttendanceResponse?> = _attendResult

    private val _getLocationInformation = MutableLiveData<LocationModel>()
    val getLocationInformation: LiveData<LocationModel> = _getLocationInformation

    fun getAccessToken(): LiveData<String> {
        return pref.getAccessToken().asLiveData()
    }

    fun getLocationInfo(context: Context, latitude: String, longitude: String) {
        GeoCoderUtil.execute(context, latitude, longitude, object: LoadDataCallback<LocationModel> {
            override fun onDataLoaded(response: LocationModel) {
                _getLocationInformation.value = response
            }
            override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                Log.e(TAG, reasonMsg)
            }
        })
    }

    fun addAttend(token: String, type: RequestBody, imageMultipart: MultipartBody.Part, lat: RequestBody, long: RequestBody){
        _isLoading.value = true
        val client = ApiConfig.getApiService().addAttend(
            "Bearer $token",
            type,
            lat,
            long,
            imageMultipart
        )
        client.enqueue(object : Callback<PostAttendanceResponse> {
            override fun onResponse(
                call: Call<PostAttendanceResponse>,
                response: Response<PostAttendanceResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _attendResult.value = responseBody
                        _isMessage.value = Event(responseBody.message)
                    }
                } else {
                    _isLoading.value = false
                    _isMessage.value = Event(response.message())
                }
            }

            override fun onFailure(call: Call<PostAttendanceResponse>, t: Throwable) {
                _isLoading.value = false
                _isMessage.value = Event(t.message)
            }

        })
    }

    fun setLocation(location: String){
        _location.value = location
    }

    fun setLatitude(location: String){
        _latitude.value = location
    }

    fun setLongitude(location: String){
        _longitude.value = location
    }
}