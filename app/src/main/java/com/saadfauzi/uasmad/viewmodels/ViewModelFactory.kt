package com.saadfauzi.uasmad.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.ui.jeniscuti.JenisCutiViewModel
import com.saadfauzi.uasmad.ui.listpresence.ListPresenceViewModel
import com.saadfauzi.uasmad.ui.presence.PresenceViewModel

class ViewModelFactory (private val context: Context, private val pref: CustomSettingPreferences): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(PresenceViewModel::class.java) -> {
                PresenceViewModel(pref) as T
            }
            modelClass.isAssignableFrom(ListPresenceViewModel::class.java) -> {
                ListPresenceViewModel(pref) as T
            }
            modelClass.isAssignableFrom(JenisCutiViewModel::class.java) -> {
                JenisCutiViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class " + modelClass.name)
        }
    }
}