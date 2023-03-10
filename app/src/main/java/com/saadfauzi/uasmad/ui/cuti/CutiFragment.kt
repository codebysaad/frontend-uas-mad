package com.saadfauzi.uasmad.ui.cuti

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saadfauzi.uasmad.R
import com.saadfauzi.uasmad.adapters.ListCutiAdapter
import com.saadfauzi.uasmad.databinding.FragmentCutiBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.models.Cuti
import com.saadfauzi.uasmad.ui.jeniscuti.JenisCutiFragmentDirections
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CutiFragment : Fragment() {

    private val binding by lazy {
        FragmentCutiBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: CutiViewModel

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
                CutiViewModel::class.java
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
            viewModel.getAllCuti(it)
            Log.d("TokenJenisCuti", it)
        }

        viewModel.listCuti.observe(viewLifecycleOwner) {
            if (it != null) {
                setupRecyclerView(it)
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) {
            if (it != null) {
                showToast(it.message)
            }
        }

        binding.fabCuti.setOnClickListener {
            findNavController().navigate(R.id.action_nav_cuti_to_nav_add_cuti)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(listData: ArrayList<Cuti>) {
        binding.rvListCuti.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ListCutiAdapter(listData, object : ListCutiAdapter.OnAdapterListener{
            override fun onDelete(data: Cuti) {
                deleteDialog(data)
            }

            override fun onUpdate(data: Cuti) {
                val action = CutiFragmentDirections.actionNavCutiToNavUpdateCuti(data)
                findNavController().navigate(action)
            }
        })
        binding.rvListCuti.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbListCuti.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun deleteDialog(data: Cuti) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage(resources.getString(R.string.dialog_delete))
        builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            viewModel.getAccessToken().observe(viewLifecycleOwner) {
                viewModel.deleteCuti(it, data)
                viewModel.getAllCuti(it)
                Log.d("TokenJenisCuti", it)
            }
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ -> dialogInterface.cancel() }
        builder.show()
    }
}