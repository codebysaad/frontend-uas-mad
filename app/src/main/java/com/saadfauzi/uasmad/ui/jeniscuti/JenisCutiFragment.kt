package com.saadfauzi.uasmad.ui.jeniscuti

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
import androidx.core.os.bundleOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saadfauzi.uasmad.R
import com.saadfauzi.uasmad.adapters.ListJenisCutiAdapter
import com.saadfauzi.uasmad.databinding.FragmentJenisCutiBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.models.JenisCuti
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class JenisCutiFragment : Fragment() {

    private val binding by lazy {
        FragmentJenisCutiBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: JenisCutiViewModel
//    private var token: String = "3|C8NASZ7I7qn35UWEdn3TPJbCZjPYODZbkStIydUI"

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

        viewModel.getAccessToken().observe(viewLifecycleOwner) {
//            token = it
            viewModel.getAllJenisCuti(it)
            Log.d("TokenJenisCuti", it)
        }

        viewModel.listJenisCuti.observe(viewLifecycleOwner) {
            if (it != null) {
                setupRecyclerView(it)
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) {
            if (it != null) {
                showToast(it.message)
            }
        }

        binding.fabJenisCuti.setOnClickListener {
            findNavController().navigate(R.id.action_nav_jns_cuti_to_nav_add_jns_cuti)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(listData: ArrayList<JenisCuti>) {
        binding.rvListJenisCuti.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ListJenisCutiAdapter(listData, object : ListJenisCutiAdapter.OnAdapterListener{
            override fun onDelete(data: JenisCuti) {
                deleteDialog(data)
            }

            override fun onUpdate(data: JenisCuti) {
                val action = JenisCutiFragmentDirections.actionNavJnsCutiToNavUpdateJnsCuti(data)
                findNavController().navigate(action)
            }
        })
        binding.rvListJenisCuti.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbListJenisCuti.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun deleteDialog(data: JenisCuti) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage(resources.getString(R.string.dialog_delete))
        builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            viewModel.getAccessToken().observe(viewLifecycleOwner) {
                viewModel.deleteJenisCuti(it, data)
                viewModel.getAllJenisCuti(it)
                Log.d("TokenJenisCuti", it)
            }
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ -> dialogInterface.cancel() }
        builder.show()
    }
}