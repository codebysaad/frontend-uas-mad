package com.saadfauzi.uasmad.ui.presence

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.saadfauzi.uasmad.databinding.FragmentPresenceBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.helper.reduceFileImage
import com.saadfauzi.uasmad.helper.uriToFile
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Activity.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val binding by lazy {
        FragmentPresenceBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: PresenceViewModel
    private val typeAttend = arrayOf("Masuk", "Pulang")
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var token: String
    private lateinit var type: String
    private lateinit var lat: String
    private lateinit var long: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val pref = CustomSettingPreferences.getInstance(requireActivity().dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(requireContext(), pref))[
                PresenceViewModel::class.java
        ]

        getMyLastLocation()

        viewModel.getAccessToken().observe(viewLifecycleOwner) {
            token = it
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        binding.spinnerType.onItemSelectedListener = this
        val adapter = ArrayAdapter(requireActivity(), R.layout.simple_spinner_item, typeAttend)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter

        binding.apply {
            btnGallery.setOnClickListener {
                startGallery()
            }
            btnCamera.setOnClickListener {
                takePhoto()
            }
            btnUpload.setOnClickListener {
                addAttendance()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getMyLastLocation()
        viewModel.location.observe(viewLifecycleOwner) {
            binding.edtLatLong.setText(it)
        }
        viewModel.latitude.observe(viewLifecycleOwner) {
            lat = it
        }
        viewModel.longitude.observe(viewLifecycleOwner) {
            long = it
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        type = typeAttend[position]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            pbAttend.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnUpload.isEnabled = !isLoading
            btnCamera.isEnabled = !isLoading
            btnGallery.isEnabled = !isLoading
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = context?.let { uriToFile(selectedImg, it) }
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val myFile = File(currentPhotoPath)
                getFile = myFile
                val result = BitmapFactory.decodeFile(myFile.path)
                binding.previewImageView.setImageBitmap(result)
            }
        }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        com.saadfauzi.uasmad.helper.createTempFile(requireActivity().application).also { file ->
            val photoURI: Uri = FileProvider.getUriForFile(
                requireActivity(),
                "com.saadfauzi.uasmad",
                file
            )
            currentPhotoPath = file.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun addAttendance() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            viewModel.getAccessToken().observe(viewLifecycleOwner) {
                Log.d("AddNewFragment", it)
                token = it
            }

            viewModel.addAttend(
                token,
                type.toRequestBody("text/plain".toMediaType()),
                imageMultipart,
                lat.toRequestBody("text/plain".toMediaType()),
                long.toRequestBody("text/plain".toMediaType()),
            )
        } else {
            showLoading(false)
            Toast.makeText(
                activity,
                "Please insert image first!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getMyLastLocation() {
        var currentLocation = ""
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d("Location", "Lat: ${location.latitude} Lon: ${location.longitude}")
                    viewModel.setLatitude(location.latitude.toString())
                    viewModel.setLongitude(location.longitude.toString())
                    viewModel.getLocationInfo(
                        requireContext(),
                        location.latitude.toString(),
                        location.longitude.toString()
                    )
                    viewModel.getLocationInformation.observe(viewLifecycleOwner) {
                        currentLocation = it.locationAddress
                    }
                    viewModel.setLocation(currentLocation)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "No Location Found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    Toast.makeText(
                        requireActivity(),
                        "Sorry Location Access Not Allowed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        activity?.let { base ->
            ContextCompat.checkSelfPermission(
                base.baseContext,
                it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}