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
import androidx.navigation.fragment.navArgs
import com.saadfauzi.uasmad.R
import com.saadfauzi.uasmad.databinding.FragmentUpdateJenisCutiBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UpdateJenisCutiFragment : Fragment() {
    private val binding by lazy {
        FragmentUpdateJenisCutiBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: JenisCutiViewModel

    private val args: UpdateJenisCutiFragmentArgs by navArgs()

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
            edtUpdateJnsCuti.setText(args.jenisCuti.jenisCuti)
            edtUpdateDescJnsCuti.setText(args.jenisCuti.deskripsi)
        }

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

        viewModel.resultUpdateJenisCuti.observe(viewLifecycleOwner) { data->
            if (data != null){
                findNavController().popBackStack()
            }
        }
        binding.btnUpdate.setOnClickListener {
            if (validateInput()) {
                val jnsCuti =
                    binding.edtUpdateJnsCuti.text.toString().toRequestBody("text/plain".toMediaType())
                val desc =
                    binding.edtUpdateDescJnsCuti.text.toString().toRequestBody("text/plain".toMediaType())
                val idBody = args.jenisCuti.id.toString().toRequestBody("text/plain".toMediaType())
                val idParams = args.jenisCuti.id
                viewModel.getAccessToken().observe(viewLifecycleOwner) {
                    viewModel.updateJenisCuti(
                        it,
                        idBody,
                        jnsCuti,
                        desc,
                        idParams,
                    )
                    Log.d("TokenJenisCuti", it)
                }
            } else {
                binding.edtUpdateJnsCuti.error = "Kolom ini tidak boleh kosong"
                binding.edtUpdateDescJnsCuti.error = "Kolom ini tidak boleh kosong"
            }
        }
    }

    private fun validateInput(): Boolean {
        val jnsCuti = binding.edtUpdateJnsCuti.text.toString()
        val desc = binding.edtUpdateDescJnsCuti.text.toString()
        return jnsCuti.isNotEmpty() && desc.isNotEmpty()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbUpdateJenisCuti.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}