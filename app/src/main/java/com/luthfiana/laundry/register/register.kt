package com.luthfiana.laundry.register

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luthfiana.laundry.R
import com.luthfiana.laundry.login.Login
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var etNama: EditText
    private lateinit var etNohp: EditText
    private lateinit var etKataSandi: EditText
    private lateinit var etCabang: EditText
    private lateinit var bDaftar: Button
    private lateinit var tvSudahPunyaAkun: TextView
    private lateinit var spRole: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        spRole = findViewById(R.id.spRole)
        etNama = findViewById(R.id.etNama)
        etNohp = findViewById(R.id.etNohp)
        etKataSandi = findViewById(R.id.etKataSandi)
        etCabang = findViewById(R.id.etCabang)
        bDaftar = findViewById(R.id.bDaftar)
        tvSudahPunyaAkun = findViewById(R.id.tvsudahpunyaakun)

        // Setup spinner adapter (ONLY DO THIS ONCE)
        val roles = arrayOf("Pegawai", "Kepala Cabang", "Admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRole.adapter = adapter

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("users")

        bDaftar.setOnClickListener {
            registerUser()
        }

        tvSudahPunyaAkun.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
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

    private fun registerUser() {
        val nama = etNama.text.toString().trim()
        val nohp = etNohp.text.toString().trim()
        val kataSandi = etKataSandi.text.toString().trim()
        val cabang = etCabang.text.toString().trim()

        if (nama.isEmpty() || nohp.isEmpty() || kataSandi.isEmpty() || cabang.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate random user ID
        val userId = UUID.randomUUID().toString()

        // Check if phone number already exists
        database.orderByChild("nohp").equalTo(nohp).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@RegisterActivity, "Nomor telepon sudah terdaftar", Toast.LENGTH_SHORT).show()
                } else {
                    // Create user data
                    val role = when (spRole.selectedItem.toString()) {
                        "Admin" -> "admin"
                        "Kepala Cabang" -> "kepala_cabang"
                        else -> "pegawai"
                    }

                    val user = HashMap<String, Any>()
                    user["id"] = userId // This should match what you're retrieving later
                    user["nama"] = nama
                    user["nohp"] = nohp
                    user["kataSandi"] = kataSandi
                    user["cabang"] = cabang
                    user["role"] = role

                    // Save to Firebase
                    database.child(userId).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(this@RegisterActivity, getString(R.string.pendaftaranberhasil), Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, Login::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@RegisterActivity, getString(R.string.gagalmendaftar), Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RegisterActivity, getString(R.string.gagalmemeriksanotelepon), Toast.LENGTH_SHORT).show()
            }
        })
    }
}