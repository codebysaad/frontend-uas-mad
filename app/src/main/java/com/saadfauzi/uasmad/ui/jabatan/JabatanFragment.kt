package com.saadfauzi.uasmad.ui.jabatan

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saadfauzi.uasmad.R
import com.saadfauzi.uasmad.adapters.ListJabatanAdapter
import com.saadfauzi.uasmad.adapters.ListJenisCutiAdapter
import com.saadfauzi.uasmad.databinding.FragmentJabatanBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.models.DataJabatan
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class JabatanFragment : Fragment() {

    private val binding by lazy {
        FragmentJabatanBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: JabatanViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        viewModel.getAccessToken().observe(viewLifecycleOwner) {
            viewModel.getAllJabatan(it)
            Log.d("TokenJenisCuti", it)
        }

        viewModel.listJabatan.observe(viewLifecycleOwner) {
            if (it != null) {
                setupRecyclerView(it)
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) {
            if (it != null) {
                showToast(it.message)
            }
        }

        binding.fabJabatan.setOnClickListener {
            findNavController().navigate(R.id.action_nav_jabatan_to_nav_add_jabatan)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(listData: ArrayList<DataJabatan>) {
        binding.rvListJabatan.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ListJabatanAdapter(listData, object : ListJabatanAdapter.OnAdapterListener{
            override fun onDelete(data: DataJabatan) {
                deleteDialog(data)
            }

            override fun onUpdate(data: DataJabatan) {
                val action = JabatanFragmentDirections.actionNavJabatanToNavUpdateJabatan(data)
                findNavController().navigate(action)
            }
        })
        binding.rvListJabatan.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbListJabatan.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun deleteDialog(data: DataJabatan) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage(resources.getString(R.string.dialog_delete))
        builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            viewModel.getAccessToken().observe(viewLifecycleOwner) {
                viewModel.deleteJenisCuti(it, data)
                viewModel.getAllJabatan(it)
                Log.d("TokenJenisCuti", it)
            }
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ -> dialogInterface.cancel() }
        builder.show()
    }

}