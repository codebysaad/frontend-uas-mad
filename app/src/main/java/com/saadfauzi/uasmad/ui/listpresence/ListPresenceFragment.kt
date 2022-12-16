package com.saadfauzi.uasmad.ui.listpresence

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.saadfauzi.uasmad.ListPresenceAdapter
import com.saadfauzi.uasmad.LoginActivity
import com.saadfauzi.uasmad.R
import com.saadfauzi.uasmad.databinding.FragmentListPresenceBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.models.ListAttendance
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ListPresenceFragment : Fragment() {

    private val binding by lazy {
        FragmentListPresenceBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: ListPresenceViewModel
    var token = "17|3fDKHGLiN2uPiMaoTWrOuCHDBwrw3vo0NhleCscx"

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
                ListPresenceViewModel::class.java
        ]

//        viewModel.getAccessToken().observe(viewLifecycleOwner) {
//            token = it
//            Log.d("Token", it)
//        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        viewModel.getAllAttend(token)
        viewModel.listAttendance.observe(viewLifecycleOwner) {
            if (it != null) {
                setupRecyclerView(it)
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) {
            if (it != null) {
                showToast(it.message)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(listData: ArrayList<ListAttendance>) {
        binding.rvListPresence.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = ListPresenceAdapter(listData, object : ListPresenceAdapter.OnAdapterListener{
            override fun onDelete(data: ListAttendance) {
                deleteDialog(data)
            }

            override fun onUpdate(data: ListAttendance) {
                TODO("Not yet implemented")
            }
        })
        binding.rvListPresence.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbListPresence.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun deleteDialog(data: ListAttendance) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage(resources.getString(R.string.dialog_delete))
        builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            viewModel.deleteAttend(token, data)
            viewModel.getAllAttend(token)
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ -> dialogInterface.cancel() }
        builder.show()
    }
}