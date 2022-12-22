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
import androidx.navigation.fragment.navArgs
import com.saadfauzi.uasmad.databinding.FragmentUpdateJabatanBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UpdateJabatanFragment : Fragment() {
    private val binding by lazy {
        FragmentUpdateJabatanBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: JabatanViewModel

    private val args: UpdateJabatanFragmentArgs by navArgs()

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

        binding.apply {
            edtUpdateNamaJabatan.setText(args.jabatan.namaJabatan)
            edtUpdateTugas.setText(args.jabatan.tugas)
        }

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

        viewModel.resultUpdateJabatan.observe(viewLifecycleOwner) { data->
            if (data != null){
                findNavController().popBackStack()
            }
        }
        binding.btnUpdate.setOnClickListener {
            if (validateInput()) {
                val namaJabatan =
                    binding.edtUpdateNamaJabatan.text.toString().toRequestBody("text/plain".toMediaType())
                val tugas =
                    binding.edtUpdateTugas.text.toString().toRequestBody("text/plain".toMediaType())
                val idBody = args.jabatan.id.toString().toRequestBody("text/plain".toMediaType())
                val idParams = args.jabatan.id
                viewModel.getAccessToken().observe(viewLifecycleOwner) {
                    viewModel.updateJabatan(
                        it,
                        idBody,
                        namaJabatan,
                        tugas,
                        idParams,
                    )
                    Log.d("TokenJenisCuti", it)
                }
            } else {
                binding.edtUpdateNamaJabatan.error = "Kolom ini tidak boleh kosong"
                binding.edtUpdateTugas.error = "Kolom ini tidak boleh kosong"
            }
        }
    }

    private fun validateInput(): Boolean {
        val namaJabatan = binding.edtUpdateNamaJabatan.text.toString()
        val tugas = binding.edtUpdateTugas.text.toString()
        return namaJabatan.isNotEmpty() && tugas.isNotEmpty()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbUpdateJabatan.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}