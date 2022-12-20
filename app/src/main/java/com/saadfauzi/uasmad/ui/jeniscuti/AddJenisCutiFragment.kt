package com.saadfauzi.uasmad.ui.jeniscuti

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.saadfauzi.uasmad.R
import com.saadfauzi.uasmad.databinding.FragmentAddJenisCutiBinding
import com.saadfauzi.uasmad.databinding.FragmentJenisCutiBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddJenisCutiFragment : Fragment() {

    private val binding by lazy {
        FragmentAddJenisCutiBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: JenisCutiViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = CustomSettingPreferences.getInstance(requireActivity().dataStore)

        viewModel = ViewModelProvider(this, ViewModelFactory(requireContext(), pref))[
                JenisCutiViewModel::class.java
        ]

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        viewModel.resultAddJenisCuti.observe(viewLifecycleOwner) { data->
            if (data != null){
                findNavController().popBackStack()
            }
        }
        binding.btnSubmit.setOnClickListener {
            if (validateInput()) {
                val jnsCuti =
                    binding.edtJnsCuti.text.toString().toRequestBody("text/plain".toMediaType())
                val desc =
                    binding.edtDescJnsCuti.text.toString().toRequestBody("text/plain".toMediaType())
                viewModel.getAccessToken().observe(viewLifecycleOwner) {
                    viewModel.addJenisCuti(
                        it,
                        jnsCuti,
                        desc
                    )
                    Log.d("TokenJenisCuti", it)
                }
            } else {
                binding.edtJnsCuti.error = "Kolom ini tidak boleh kosong"
                binding.edtDescJnsCuti.error = "Kolom ini tidak boleh kosong"
            }
        }
    }

    private fun validateInput(): Boolean {
        val jnsCuti = binding.edtJnsCuti.text.toString()
        val desc = binding.edtDescJnsCuti.text.toString()
        return jnsCuti.isNotEmpty() && desc.isNotEmpty()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbAddJenisCuti.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}