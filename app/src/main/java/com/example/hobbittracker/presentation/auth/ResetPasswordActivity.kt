package com.example.hobbittracker.presentation.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hobbittracker.databinding.ActivityResetPasswordBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetPasswordActivity : AppCompatActivity() {

    private val tag = this.javaClass.simpleName

    private val vm: AuthViewModel by viewModel()

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnResetPasswordSend.setOnClickListener {
            if (validateEmail()) {
                vm.resetPassword(
                    binding.tietResetPasswordEmail.text.toString(),
                    this
                )
            }
        }

        binding.btnResetPasswordCancel.setOnClickListener {
            finish()
        }

        binding.tvResetPasswordLoginnow.setOnClickListener {
            vm.startActivity(this, LoginActivity::class.java, clearTasks = true, finish = true)
        }

        vm.toast.observe(this) { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                vm.onToastShown()
            }
        }
    }

    private fun validateEmail(): Boolean {
        return AuthService.textViewValidateHandler(
            AuthService.EmailValidator(), binding.tietResetPasswordEmail
        )
    }
}