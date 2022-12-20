package com.saadfauzi.uasmad.ui.pegawai

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
import com.saadfauzi.uasmad.adapters.ListPegawaiAdapter
import com.saadfauzi.uasmad.databinding.FragmentPegawaiBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.models.DataPegawai
import com.saadfauzi.uasmad.ui.jeniscuti.JenisCutiFragmentDirections
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PegawaiFragment : Fragment() {

    private val binding by lazy {
        FragmentPegawaiBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: PegawaiViewModel

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

        viewModel.getAccessToken().observe(viewLifecycleOwner) {
            viewModel.getAllPegawai(it)
            Log.d("TokenJenisCuti", it)
        }

        viewModel.listPegawai.observe(viewLifecycleOwner) {
            if (it != null) {
                setupRecyclerView(it)
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) {
            if (it != null) {
                showToast(it.message)
            }
        }

        binding.fabPegawai.setOnClickListener {
            findNavController().navigate(R.id.action_nav_pegawai_to_nav_add_pegawai)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(listData: ArrayList<DataPegawai>) {
        binding.rvListPegawai.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ListPegawaiAdapter(listData, object : ListPegawaiAdapter.OnAdapterListener{
            override fun onDelete(data: DataPegawai) {
                deleteDialog(data)
            }

            override fun onUpdate(data: DataPegawai) {
                val action = PegawaiFragmentDirections.actionNavPegawaiToNavUpdatePegawai(data)
                findNavController().navigate(action)
            }
        })
        binding.rvListPegawai.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbListPegawai.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun deleteDialog(data: DataPegawai) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage(resources.getString(R.string.dialog_delete))
        builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            viewModel.getAccessToken().observe(viewLifecycleOwner) {
                viewModel.deleteJenisCuti(it, data)
                viewModel.getAllPegawai(it)
                Log.d("TokenJenisCuti", it)
            }
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ -> dialogInterface.cancel() }
        builder.show()
    }
}