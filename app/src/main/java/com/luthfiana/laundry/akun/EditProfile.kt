package com.luthfiana.laundry.akun

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luthfiana.laundry.R

class EditProfile : AppCompatActivity() {
    private lateinit var etNama: EditText
    private lateinit var etNohp: EditText
    private lateinit var etKataSandi: EditText
    private lateinit var etCabang: EditText
    private lateinit var bSimpan: Button
    private lateinit var ivTogglePassword: ImageView

    private var isEditingEnabled = false
    private var originalPhone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi view
        etNama = findViewById(R.id.etNama)
        etNohp = findViewById(R.id.etNohp)
        etKataSandi = findViewById(R.id.etKataSandi)
        etCabang = findViewById(R.id.etCabang)
        bSimpan = findViewById(R.id.bSimpanPerubahan)
        ivTogglePassword = findViewById(R.id.ivTogglePassword)

        // Set teks awal tombol
        bSimpan.text = getString(R.string.sunting)

        // Ambil data dari intent
        intent?.let {
            etNama.setText(it.getStringExtra("nama"))
            etNohp.setText(it.getStringExtra("nohp"))
            originalPhone = it.getStringExtra("nohp") ?: ""
            etKataSandi.setText(it.getStringExtra("kataSandi"))
            etCabang.setText(it.getStringExtra("cabang"))
        }

        // Nonaktifkan editing awal
        setEditingEnabled(false)

        // Toggle password visibility
        ivTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // Tombol simpan/sunting perubahan
        bSimpan.setOnClickListener {
            if (isEditingEnabled) {
                // Mode simpan - simpan perubahan
                saveChanges()
            } else {
                // Mode sunting - aktifkan editing
                enableEditingMode()
            }
        }
    }

    private fun enableEditingMode() {
        isEditingEnabled = true
        setEditingEnabled(true)
        bSimpan.text = getString(R.string.perbarui)

        // Beri fokus ke field pertama
        etNama.requestFocus()
    }

    private fun setEditingEnabled(enabled: Boolean) {
        etNama.isEnabled = enabled
        etNohp.isEnabled = enabled
        etKataSandi.isEnabled = enabled
        etCabang.isEnabled = enabled
    }

    private fun togglePasswordVisibility() {
        if (etKataSandi.inputType == 129) { // 129 = textPassword
            etKataSandi.inputType = 1 // 1 = text
            ivTogglePassword.setImageResource(R.drawable.view)
        } else {
            etKataSandi.inputType = 129
            ivTogglePassword.setImageResource(R.drawable.eye)
        }
        etKataSandi.setSelection(etKataSandi.text.length)
    }

    private fun saveChanges() {
        val nama = etNama.text.toString().trim()
        val nohp = etNohp.text.toString().trim()
        val kataSandi = etKataSandi.text.toString().trim()
        val cabang = etCabang.text.toString().trim()

        if (nama.isEmpty() || nohp.isEmpty() || kataSandi.isEmpty() || cabang.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_fieldkosong), Toast.LENGTH_SHORT).show()
            return
        }

        // Validasi nomor telepon
        if (!nohp.matches(Regex("^[0-9]+\$"))) {
            Toast.makeText(this, getString(R.string.toast_nohp_invalid), Toast.LENGTH_SHORT).show()
            return
        }

        // Update data di Firebase
        val database = FirebaseDatabase.getInstance().reference.child("users")

        // Cari user berdasarkan nohp asli
        database.orderByChild("nohp").equalTo(originalPhone)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            // Update data user
                            val updates = HashMap<String, Any>()
                            updates["nama"] = nama
                            updates["nohp"] = nohp
                            updates["kataSandi"] = kataSandi
                            updates["cabang"] = cabang

                            userSnapshot.ref.updateChildren(updates)
                                .addOnSuccessListener {
                                    // Update SharedPreferences
                                    val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                    with(sharedPref.edit()) {
                                        putString("user_name", nama)
                                        putString("user_phone", nohp)
                                        putString("user_branch", cabang)
                                        apply() // Gunakan apply() untuk async write
                                    }

                                    Toast.makeText(this@EditProfile, getString(R.string.profilberhasildisimpan), Toast.LENGTH_SHORT).show()

                                    // Kirim result back ke akun activity
                                    setResult(RESULT_OK)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@EditProfile, getString(R.string.gagalmemperbaruiprofil), Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this@EditProfile, getString(R.string.datapenggunatidakditemukan), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditProfile, getString(R.string.toast_errordata), Toast.LENGTH_SHORT).show()
                }
            })
    }
}