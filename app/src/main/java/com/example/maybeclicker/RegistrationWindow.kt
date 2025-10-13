package com.example.maybeclicker

import android.accounts.Account
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.View
import androidx.core.content.edit
import android.content.Intent
import org.w3c.dom.Text
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible

class RegistrationWindow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration_window)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPrefs = getSharedPreferences("General", Context.MODE_PRIVATE)
        val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
        val accounts = sharedPrefs.getString("ACCOUNTS", "!")

        if (account != "!") {
            goToMain()
        }


        val accListText = findViewById<TextView>(R.id.accountListText)
        if (accounts != "!") {
            accListText.text = "Ваши аккаунты: ${getAccountList(accounts.toString()).joinToString(", ") }"
        } else {
            accListText.isVisible=false
        }

        val button = findViewById<Button>(R.id.registerButton)
        val userInput = findViewById<EditText>(R.id.userInput)


        button.setOnClickListener {
            if (isAccountExists(userInput.text.toString(), accounts.toString())) {
                sharedPrefs.edit {
                    putString("CURRENT_ACCOUNT", userInput.text.toString())
                    goToMain()
                }
            } else {
                val count = accounts.toString().count { it == '|' }
                if (count >= 10) {
                    Toast.makeText(this, "Вы достигли лимита аккаунтов (10/10)", Toast.LENGTH_LONG).show()
                } else {
                    if (isCorrectName(userInput.text.toString(), accounts.toString())) {

                        var accs = ""
                        if (accounts == "!") {
                            accs = userInput.text.toString()
                        } else {
                            accs = "$accounts|${userInput.text.toString()}"
                        }

                        sharedPrefs.edit {
                            putString("ACCOUNTS", accs)
                            putString("CURRENT_ACCOUNT", userInput.text.toString())
                        }

                        goToMain()


                    } else {
                        Toast.makeText(this, "Error: допустимы только цифры и буквы ru/eng", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // чтобы нельзя было вернуться назад
    }

    private fun isCorrectName(name: String, accounts: String): Boolean {
        if (name.length >= 24 || name.isEmpty()) return false
        for (char in name) {
            if (!(char.lowercaseChar() in "1234567890abcdefghijklmnopqrstuvwxyzабвгдеёжзийклмнопрстуфхцчшщъыьэюя"))
                return false
        }
        return true
    }
    fun isAccountExists(name: String, accounts: String): Boolean {
        // Добавляем "|" слева и справа
        val accountList = "|$accounts|"
        val nameToCheck = "|$name|"
        return accountList.contains(nameToCheck)
    }

    override fun attachBaseContext(newBase: Context) {
        val config = newBase.resources.configuration
        config.fontScale = 1.0f  // фиксируем масштаб шрифта
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    fun getAccountList(accounts : String) : List<String> {
        val list = accounts.split("|")
        return list
    }

}
