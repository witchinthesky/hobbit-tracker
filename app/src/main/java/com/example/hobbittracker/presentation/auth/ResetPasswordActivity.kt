package com.example.hobbittracker.presentation.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hobbittracker.R
import com.example.hobbittracker.databinding.ActivityResetPasswordBinding
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_reset_password.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetPasswordActivity : AppCompatActivity() {

    private val tag = this.javaClass.simpleName

    private val vm: AuthViewModel by viewModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reset_password)

        btn_reset_password_send.setOnClickListener {
            if (validateEmail()) {
                vm.resetPassword(
                    tiet_reset_password_email.text.toString(),
                    this
                )
            }
        }

       tv_reset_password_loginnow.setOnClickListener {
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
            AuthService.EmailValidator(), tiet_reset_password_email
        )
    }
}