package com.example.hobbittracker.presentation.home

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hobbittracker.databinding.ActivityHomeBinding
import com.example.hobbittracker.presentation.MainActivity
import com.example.hobbittracker.presentation.auth.AuthViewModel
import org.koin.android.ext.android.inject

class HomeActivity : AppCompatActivity() {

    private val vm: AuthViewModel by inject()

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.btnLogOut.setOnClickListener {
            logOut()
        }

        // Vew toast in bottom region if View Model change toast live data
        vm.toast.observe(this) { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                vm.onToastShown()
            }
        }

        // current user saved in viewModel, after registration, login or another query to server user
        vm.currentUserLD.observe(this) {
            binding.tvUserName.text = it.name
            binding.tvUserEmail.text = it.email
        }
    }

    private fun logOut() {
        vm.logOutUser()
        vm.startActivity(this, MainActivity::class.java, clearTasks = true)
    }
}