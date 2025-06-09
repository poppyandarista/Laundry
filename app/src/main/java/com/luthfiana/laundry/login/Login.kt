package com.luthfiana.laundry.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luthfiana.laundry.MainActivity
import com.luthfiana.laundry.R
import com.luthfiana.laundry.register.RegisterActivity

class Login : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var etNohp: EditText
    private lateinit var etKataSandi: EditText
    private lateinit var bMasuk: Button
    private lateinit var tvBelumPunyaAkun: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("users")

        // Initialize views
        etNohp = findViewById(R.id.etNohp)
        etKataSandi = findViewById(R.id.etKataSandi)
        bMasuk = findViewById(R.id.bMasuk)
        tvBelumPunyaAkun = findViewById(R.id.tvbelumpunyaakun)

        bMasuk.setOnClickListener {
            loginUser()
        }

        tvBelumPunyaAkun.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        val etPassword = findViewById<EditText>(R.id.etKataSandi)
        val ivToggle = findViewById<ImageView>(R.id.ivTogglePassword)

        var isPasswordVisible = false

        ivToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ivToggle.setImageResource(R.drawable.view) // ikon mata terbuka
            } else {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivToggle.setImageResource(R.drawable.eye) // ikon mata tertutup
            }
            etPassword.setSelection(etPassword.text.length)
        }
    }

    private fun loginUser() {
        val nohp = etNohp.text.toString().trim()
        val kataSandi = etKataSandi.text.toString().trim()

        if (nohp.isEmpty() || kataSandi.isEmpty()) {
            Toast.makeText(this, getString(R.string.validas_loginhrsdiisi), Toast.LENGTH_SHORT).show()
            return
        }

        // Query users by phone number
        database.orderByChild("nohp").equalTo(nohp).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var loginSuccess = false

                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.value as HashMap<*, *>
                        val storedPassword = user["kataSandi"] as String

                        if (kataSandi == storedPassword) {
                            // Login successful
                            loginSuccess = true
                            Toast.makeText(this@Login, getString(R.string.loginberhasil), Toast.LENGTH_SHORT).show()

                            val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("user_id", userSnapshot.key ?: "") // This should work for all roles
                                putString("user_name", user["nama"] as String)
                                putString("user_phone", user["nohp"] as String)
                                putString("user_branch", user["cabang"] as String)
                                putString("user_role", user["role"] as String)
                                apply()
                            }

                            val intent = Intent(this@Login, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                    }

                    if (!loginSuccess) {
                        Toast.makeText(this@Login, getString(R.string.katasandisalah), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Login, getString(R.string.nomortidakterdaftar), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Login, getString(R.string.gagallogin), Toast.LENGTH_SHORT).show()
            }
        })
    }
}