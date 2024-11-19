package com.example.rachabar

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.rachabar.data.Main
import com.example.rachabar.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainAdapter: MainAdapter
    private val mains = mutableListOf<Main>()
    private lateinit var sharedPreferences: SharedPreferences
    private var isShowingRecords = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)

        if (!isLoggedIn()) {
            startLoginActivity()
            return
        }

        setupRecyclerView()
        setupBackPressedCallback()

        binding.buttonCalculate.setOnClickListener {
            calculateSplit()
        }

        // Display welcome message
        val username = sharedPreferences.getString("username", "")
        binding.textViewWelcome.text = "Bem-vindo, $username!"
    }

    private fun setupBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })
    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun startLoginActivity() {
        val intent = Intent(this, activity_login::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupRecyclerView() {
        mainAdapter = MainAdapter(mains)
        binding.recyclerViewMains.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mainAdapter
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
        }
    }

    private fun calculateSplit() {
        val friendsCountText = binding.editTextFriends.text.toString()
        val totalValueText = binding.editTextTotalValue.text.toString()

        if (friendsCountText.isEmpty() || totalValueText.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val friendsCount = friendsCountText.toIntOrNull()
        val totalValue = totalValueText.toDoubleOrNull()

        if (friendsCount == null || totalValue == null || friendsCount <= 0) {
            Toast.makeText(this, "Por favor, insira valores válidos", Toast.LENGTH_SHORT).show()
            return
        }

        val splitValue = totalValue / friendsCount
        binding.textViewResult.text = "Valor por pessoa: R$ %.2f".format(splitValue)

        val newMain = Main(
            id = mains.size,
            totalAmount = totalValue,
            numberOfFriends = friendsCount,
            amountPerPerson = splitValue,
            date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        )
        mains.add(0, newMain)
        mainAdapter.updateMains(mains)

        isShowingRecords = true
        binding.textViewRecordsTitle.visibility = View.VISIBLE
        binding.recyclerViewMains.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_default, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                saveBill()
                true
            }
            R.id.menu_records -> {
                showRecords()
                true
            }
            R.id.menu_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveBill() {
        Toast.makeText(this, "Conta salva", Toast.LENGTH_SHORT).show()
    }

    private fun showRecords() {
        isShowingRecords = !isShowingRecords
        binding.textViewRecordsTitle.visibility = if (isShowingRecords) View.VISIBLE else View.GONE
        binding.recyclerViewMains.visibility = if (isShowingRecords) View.VISIBLE else View.GONE

        if (isShowingRecords) {
            if (mains.isEmpty()) {
                Toast.makeText(this, "Não há registros para exibir", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Exibindo ${mains.size} registros", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Registros ocultados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        startLoginActivity()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sair do aplicativo")
            .setMessage("Você tem certeza que deseja sair do aplicativo?")
            .setPositiveButton("Sim") { _, _ ->
                finish()
            }
            .setNegativeButton("Não", null)
            .show()
    }
}