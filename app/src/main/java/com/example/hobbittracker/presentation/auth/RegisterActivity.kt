package com.example.hobbittracker.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hobbittracker.databinding.ActivityRegisterBinding
import org.koin.android.ext.android.inject


class RegisterActivity : AppCompatActivity() {
    private val tag = this.javaClass.simpleName

    private val vm: AuthViewModel by inject()

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnRegisterLogin.setOnClickListener {
            if (validateName() && validateEmail() && validatePassword()) {
                vm.register(
                    binding.tietRegisterName.text.toString(),
                    binding.tietRegisterEmail.text.toString(),
                    binding.tietRegisterPassword.text.toString(),
                    this
                )
            }
        }

        binding.ivRegisterFacebook.setOnClickListener {
            vm.signInWithFacebook(this)
        }

        binding.ivRegisterGoogle.setOnClickListener {
            vm.registerWithGoogle(this)
        }

        binding.tvRegisterLoginnow.setOnClickListener {
            vm.startActivity(this, LoginActivity::class.java)
        }

        vm.toast.observe(this) { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                vm.onToastShown()
            }
        }

        vm.spinner.observe(this) { value ->
            value.let { show ->
                binding.spinnerRegister.visibility = if (show) View.VISIBLE else View.GONE
                Log.i(tag, "$show")
            }
        }
    }

    private fun validateName(): Boolean {
        return AuthService.textViewValidateHandler(
            AuthService.NameValidator(),  binding.tietRegisterName
        )
    }

    private fun validateEmail(): Boolean {
        return AuthService.textViewValidateHandler(
            AuthService.EmailValidator(),  binding.tietRegisterEmail
        )
    }

    private fun validatePassword(): Boolean {
        return AuthService.textViewValidateHandler(
            AuthService.PasswordValidator(),  binding.tietRegisterPassword
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vm.onActivityResult(requestCode, resultCode, data, this)
    }
}
