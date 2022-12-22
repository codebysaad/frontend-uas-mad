package com.saadfauzi.uasmad.ui.jabatan

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
import com.saadfauzi.uasmad.databinding.FragmentAddJabatanBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddJabatanFragment : Fragment() {
    private val binding by lazy {
        FragmentAddJabatanBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: JabatanViewModel

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
                JabatanViewModel::class.java
        ]

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        viewModel.resultAddJabatan.observe(viewLifecycleOwner) { data->
            if (data != null){
                findNavController().popBackStack()
            }
        }
        binding.btnSubmit.setOnClickListener {
            if (validateInput()) {
                val jnsCuti =
                    binding.edtNamaJabatan.text.toString().toRequestBody("text/plain".toMediaType())
                val desc =
                    binding.edtTugas.text.toString().toRequestBody("text/plain".toMediaType())
                viewModel.getAccessToken().observe(viewLifecycleOwner) {
                    viewModel.addJabatan(
                        it,
                        jnsCuti,
                        desc
                    )
                    Log.d("TokenJenisCuti", it)
                }
            } else {
                binding.edtNamaJabatan.error = "Kolom ini tidak boleh kosong"
                binding.edtTugas.error = "Kolom ini tidak boleh kosong"
            }
        }
    }

    private fun validateInput(): Boolean {
        val jnsCuti = binding.edtNamaJabatan.text.toString()
        val desc = binding.edtTugas.text.toString()
        return jnsCuti.isNotEmpty() && desc.isNotEmpty()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbAddJabatan.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}