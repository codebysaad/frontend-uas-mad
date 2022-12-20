package com.saadfauzi.uasmad.ui.pegawai

import android.app.Activity
import android.app.DatePickerDialog
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
import com.saadfauzi.uasmad.databinding.FragmentUpdatePegawaiBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.*

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UpdatePegawaiFragment : Fragment() {

    private val binding by lazy {
        FragmentUpdatePegawaiBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: PegawaiViewModel
    private var cal: Calendar = Calendar.getInstance()
    private val args: UpdatePegawaiFragmentArgs by navArgs()

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
            edtNamaLengkap.setText(args.pegawai.namaLengkap)
            edtAlamat.setText(args.pegawai.alamat)
            edtTmptLahir.setText(args.pegawai.tmptLahir)
            tvTglLahir.text = args.pegawai.tglLahir
        }

        viewModel = ViewModelProvider(this, ViewModelFactory(requireContext(), pref))[
                PegawaiViewModel::class.java
        ]

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        viewModel.resultUpdatePegawai.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                findNavController().popBackStack()
            }
        }

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        binding.btnTglLahir.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnUpdate.setOnClickListener {
            if (validateInput()) {
                val namaLengkap =
                    binding.edtNamaLengkap.text.toString().toRequestBody("text/plain".toMediaType())
                val alamat =
                    binding.edtAlamat.text.toString().toRequestBody("text/plain".toMediaType())
                val tmptLahir =
                    binding.edtTmptLahir.text.toString().toRequestBody("text/plain".toMediaType())
                val tglLahir =
                    binding.tvTglLahir.text.toString().toRequestBody("text/plain".toMediaType())
                val idBody = args.pegawai.id.toString().toRequestBody("text/plain".toMediaType())
                val idParams = args.pegawai.id
                viewModel.getAccessToken().observe(viewLifecycleOwner) {
                    viewModel.updatePegawai(
                        it,
                        idBody,
                        namaLengkap,
                        alamat,
                        tmptLahir,
                        tglLahir,
                        idParams,
                    )
                    Log.d("TokenJenisCuti", it)
                }
            } else {
                binding.edtNamaLengkap.error = "Kolom ini tidak boleh kosong"
                binding.edtAlamat.error = "Kolom ini tidak boleh kosong"
                binding.edtTmptLahir.error = "Kolom ini tidak boleh kosong"
            }
        }
    }

    private fun validateInput(): Boolean {
        val namaLengkap = binding.edtNamaLengkap.text.toString()
        val alamat = binding.edtAlamat.text.toString()
        val tmptLahir = binding.edtTmptLahir.text.toString()
        val tglLahir = binding.tvTglLahir.text.toString()
        return namaLengkap.isNotEmpty() && alamat.isNotEmpty() && tmptLahir.isNotEmpty() && tglLahir.isNotEmpty()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbUpdatePegawai.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.tvTglLahir.text = sdf.format(cal.time)
    }
}