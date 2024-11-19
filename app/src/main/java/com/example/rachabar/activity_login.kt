package com.example.rachabar

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rachabar.database.DatabaseHelper
import com.example.rachabar.databinding.ActivityLoginBinding

class activity_login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)

        // Check if user is already logged in
        if (isLoggedIn()) {
            startMainActivity()
            return
        }

        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                if (dbHelper.getUser(username, password)) {
                    loginUser(username)
                    startMainActivity()
                } else {
                    Toast.makeText(this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val userId = dbHelper.addUser(username, password)
                if (userId != -1L) {
                    Toast.makeText(this, "Usuário registrado com sucesso", Toast.LENGTH_SHORT).show()
                    loginUser(username)
                    startMainActivity()
                } else {
                    Toast.makeText(this, "Erro ao registrar usuário", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun loginUser(username: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("username", username)
        editor.apply()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}