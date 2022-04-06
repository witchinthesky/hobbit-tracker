package com.example.hobbittracker.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hobbittracker.databinding.ActivityLoginBinding
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {

    private val vm: AuthViewModel by inject()

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnLoginLogin.setOnClickListener {
            if (validateEmail() && validatePassword()) {
                vm.login(
                    binding.tietLoginEmail.text.toString(),
                    binding.tietLoginPassword.text.toString(),
                    this
                )
            }
        }

        binding.ivLoginFacebook.setOnClickListener {
            vm.signInWithFacebook(this)
        }

        binding.ivLoginGoogle.setOnClickListener {
            vm.loginWithGoogle(this)
        }

        binding.tvLoginRegsiternow.setOnClickListener {
            vm.startActivity(this, RegisterActivity::class.java)
        }

        binding.tvLoginForgotPassword.setOnClickListener {
            vm.startActivity(this, ResetPasswordActivity::class.java)
        }

        binding.tietLoginPassword.setAutofillHints(View.AUTOFILL_HINT_PASSWORD);
        binding.tietLoginEmail.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS);

        vm.toast.observe(this) { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                vm.onToastShown()
            }
        }
    }

    private fun validateEmail(): Boolean {
        return AuthService.textViewValidateHandler(
            AuthService.EmailValidator(), binding.tietLoginEmail
        )
    }

    private fun validatePassword(): Boolean {
        return AuthService.textViewValidateHandler(
            AuthService.PasswordValidator(), binding.tietLoginPassword
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vm.onActivityResult(requestCode, resultCode, data, this)
    }
}
