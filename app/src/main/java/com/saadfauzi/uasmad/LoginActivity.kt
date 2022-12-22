package com.saadfauzi.uasmad

import android.content.Context
import android.content.Intent
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.saadfauzi.uasmad.databinding.ActivityLoginBinding
import com.saadfauzi.uasmad.helper.CustomSettingPreferences
import com.saadfauzi.uasmad.viewmodels.RegisterLoginViewModel
import com.saadfauzi.uasmad.viewmodels.ViewModelFactory
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: RegisterLoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        moveToRegister()

        initViewModels()
        setButtonLoginEnabled()
        initEditText()

        binding.btnLogin.setOnClickListener {
            getLogin()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        exitDialog()
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
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun moveToRegister() {
        val spannableString = SpannableString(resources.getString(R.string.don_t_have_an_account))
        val register: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        spannableString.setSpan(register, 18, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvNotHavAccount.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
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
                    if (p0.length < 6) {
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
        binding.btnLogin.isEnabled =
            emailRes != null && passRes != null && emailRes.contains("@") && passRes.length >= 6
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun getLogin() {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        viewModel.postLogin(email, password)
        viewModel.loginResult.observe(this) { result ->
            if (result != null) {
                val token = result.accessToken.toString()
                result.data?.name?.let { name->
                    result.data.email?.let { email ->
                        result.data.photo?.let { photo ->
                            viewModel.saveAccessToken(
                                true,
                                token,
                                name,
                                email,
                                photo,
                            )
                        }
                    }
                }
                Log.d("LoginActivity", token)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                showToast(resources.getString(R.string.failed_login))
            }
        }
    }

    private fun exitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.app_name))
        builder.setMessage(resources.getString(R.string.dialog_exit_app))
        builder.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
            finish()
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ -> dialogInterface.cancel() }
        builder.show()
    }
}