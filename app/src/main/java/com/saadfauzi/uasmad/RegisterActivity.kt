package com.saadfauzi.uasmad

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.saadfauzi.uasmad.databinding.ActivityRegisterBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.helper.reduceFileImage
import com.saadfauzi.uasmad.helper.uriToFile
import com.saadfauzi.uasmad.viewmodels.RegisterLoginViewModel
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val roleUser = arrayListOf("Admin", "User")
    private lateinit var role: String
    private lateinit var viewModel: RegisterLoginViewModel
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        backToLogin()
        setUpSpinner(roleUser)
        initEditText()
        initViewModels()

        binding.btnGallery.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Choose a Picture")
            launcherIntentGallery.launch(chooser)
        }

        binding.btnRegister.setOnClickListener { getRegister() }
    }

    override fun onStart() {
        super.onStart()
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun initViewModels() {
        val pref = CustomSettingPreferences.getInstance(dataStore)

        viewModel = ViewModelProvider(this, ViewModelFactory(this, pref))[
                RegisterLoginViewModel::class.java
        ]

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.getStateLogin().observe(this) {
            if (it) {
                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                finish()
            }
        }

        viewModel.registerResult.observe(this) { result ->
            if (result != null) {
                if (result.accessToken == null) {
                    result.tokenType?.let { Log.d("RegisterActivity", it) }
                    Toast.makeText(this, result.tokenType, Toast.LENGTH_SHORT).show()
                } else {
                    val token = result.accessToken.toString()
                    viewModel.saveAccessToken(true, token)
                    Log.d("RegisterActivity", token)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(this, resources.getString(R.string.failed_login), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun initEditText() {
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.contains("@") == false) {
                    binding.edtEmail.error = resources.getString(R.string.error_email)
                }
                setButtonLoginEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length != null) {
                    if (p0.length < 8) {
                        binding.edtPassword.error = resources.getString(R.string.error_password)
                    }
                }
                setButtonLoginEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun setButtonLoginEnabled() {
        val emailRes = binding.edtEmail.text
        val passRes = binding.edtPassword.text
        binding.btnRegister.isEnabled =
            emailRes != null && passRes != null && emailRes.contains("@") && passRes.length >= 8
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }

    private fun setUpSpinner(data: ArrayList<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter

        binding.spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                role = data[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun backToLogin() {
        val spannableString = SpannableString(resources.getString(R.string.already_have_an_account))
        val register: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        spannableString.setSpan(register, 18, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvAlreadyHaveAccount.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun getRegister() {
        if (getFile != null) {
            val name = binding.edtUsername.text.toString().toRequestBody("text/plain".toMediaType())
            val email = binding.edtEmail.text.toString().toRequestBody("text/plain".toMediaType())
            val phoneNumber =
                binding.edtPhoneNumber.text.toString().toRequestBody("text/plain".toMediaType())
            val password =
                binding.edtPassword.text.toString().toRequestBody("text/plain".toMediaType())
            val roleInput = role.toRequestBody("text/plain".toMediaType())
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            viewModel.postRegister(
                name,
                email,
                phoneNumber,
                password,
                roleInput,
                imageMultipart
            )

        } else {
            showLoading(false)
            Toast.makeText(
                this,
                "Please insert image first!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        this.let { base ->
            ContextCompat.checkSelfPermission(
                base.baseContext,
                it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}