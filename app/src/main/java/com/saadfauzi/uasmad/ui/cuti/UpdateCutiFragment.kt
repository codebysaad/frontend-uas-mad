package com.saadfauzi.uasmad.ui.cuti

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.saadfauzi.uasmad.databinding.FragmentUpdateCutiBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.*

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UpdateCutiFragment : Fragment() {

    private val binding by lazy {
        FragmentUpdateCutiBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: CutiViewModel
    private lateinit var jenisCuti: String
    private var cal: Calendar = Calendar.getInstance()
    private lateinit var idBody: String
    private var idParams: Int = 0

    private val args: UpdateCutiFragmentArgs by navArgs()

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
            idBody = args.cuti.id.toString()
            idParams = args.cuti.id
            edtAlasanCuti.setText(args.cuti.alasan)
            tvTglAwal.text = args.cuti.tglAwal
            tvTglAkhir.text = args.cuti.tglAkhir
        }

        viewModel = ViewModelProvider(this, ViewModelFactory(requireContext(), pref))[
                CutiViewModel::class.java
        ]

        viewModel.getAccessToken().observe(viewLifecycleOwner) {
            viewModel.getAllJenisCuti(it)
            Log.d("TokenJenisCuti", it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        viewModel.resultUpdateCuti.observe(viewLifecycleOwner) { data->
            if (data != null){
                findNavController().popBackStack()
            }
        }

        viewModel.listJenisCuti.observe(viewLifecycleOwner) { jenisCuti ->
            if (jenisCuti != null) {
                val arraySpinner: ArrayList<Data> = ArrayList()
                for (i in jenisCuti) {
                    val data = Data(
                        i.id,
                        i.jenisCuti
                    )
                    arraySpinner.add(data)
                }
                setUpSpinner(arraySpinner)
            }
        }

        val dateSetListenerAwal =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInViewAwal()
            }

        val dateSetListenerAkhir =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInViewAkhir()
            }

        binding.apply {
            btnTglAwal.setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    dateSetListenerAwal,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            btnTglAkhir.setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    dateSetListenerAkhir,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            btnUpdate.setOnClickListener {
                if (validateInput()) {
                    val alasan = binding.edtAlasanCuti.text.toString().toRequestBody()
                    val jnsCuti = jenisCuti.toRequestBody()
                    val tglAwal = binding.tvTglAwal.text.toString().toRequestBody()
                    val tglAkhir = binding.tvTglAkhir.text.toString().toRequestBody()
                    val status = "1".toRequestBody()
                    viewModel.getAccessToken().observe(viewLifecycleOwner) {
                        viewModel.updateCuti(
                            it,
                            idBody.toRequestBody(),
                            jnsCuti,
                            alasan,
                            tglAwal,
                            tglAkhir,
                            status,
                            idParams
                        )
                    }
                }else{
                    showToast("Form harus diisi semua")
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        val alasan = binding.edtAlasanCuti.text.toString()
        val idInput = jenisCuti
        val tglAwal = binding.tvTglAwal.text.toString()
        val tglAkhir = binding.tvTglAkhir.text.toString()
        return alasan.isNotEmpty() && idInput.isNotEmpty() && tglAwal.isNotEmpty() && tglAkhir.isNotEmpty()
    }

    private fun setUpSpinner(data: ArrayList<Data>) {
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJnsCuti.adapter = adapter
        data.forEachIndexed{index, dataSpinner ->
            if (dataSpinner.id == idParams){
                binding.spinnerJnsCuti.setSelection(index)
                Log.i("IndexSpinner", "Spinner: ${dataSpinner.id}, Index: ${idParams}")
            }
        }
        binding.spinnerJnsCuti.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                jenisCuti = data[position].id.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun updateDateInViewAwal() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.tvTglAwal.text = sdf.format(cal.time)
    }

    private fun updateDateInViewAkhir() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.tvTglAkhir.text = sdf.format(cal.time)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbUpdateCuti.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}