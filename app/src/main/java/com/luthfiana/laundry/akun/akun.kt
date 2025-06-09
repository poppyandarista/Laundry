package com.luthfiana.laundry.akun

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luthfiana.laundry.R
import com.luthfiana.laundry.login.Login

class akun : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_akun)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil data dari SharedPreferences
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val phoneNumber = sharedPref.getString("user_phone", "") ?: ""

        // Jika menggunakan SharedPreferences saja
        val userName = sharedPref.getString("user_name", "") ?: ""
        val userBranch = sharedPref.getString("user_branch", "") ?: ""
        val userId = sharedPref.getString("user_id", "") ?: ""
        findViewById<TextView>(R.id.tvIdUser_isi).text = userId
        // Set data ke view
        findViewById<TextView>(R.id.tvNamaUser).text = userName
        findViewById<TextView>(R.id.tvCabang_isi).text = userBranch

        // Atau ambil data lengkap dari Firebase
        if (phoneNumber.isNotEmpty()) {
            loadUserDataFromFirebase(phoneNumber)
        }

        // Tombol logout
        findViewById<TextView>(R.id.tvLogout).setOnClickListener {
            logoutUser()
        }
        // Tombol edit profile
        // Di dalam onCreate() akun.kt
        findViewById<Button>(R.id.bEditProfile).setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            // Kirim data user yang sedang login ke EditProfile
            intent.putExtra("nama", findViewById<TextView>(R.id.tvNamaUser).text.toString())
            intent.putExtra("nohp", findViewById<TextView>(R.id.tvNoHp_isi).text.toString())
            intent.putExtra("kataSandi", findViewById<TextView>(R.id.tvKataSandi_isi).text.toString())
            intent.putExtra("cabang", findViewById<TextView>(R.id.tvCabang_isi).text.toString())
            startActivityForResult(intent, 1) // Gunakan startActivityForResult
        }
    }

    private fun loadUserDataFromFirebase(phoneNumber: String) {
        val database = FirebaseDatabase.getInstance().reference.child("users")

        database.orderByChild("nohp").equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.value as? HashMap<*, *>

                            user?.let {
                                // Save the ID to SharedPreferences as well
                                val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putString("user_id", userSnapshot.key ?: "")
                                    apply()
                                }

                                // Update UI
                                findViewById<TextView>(R.id.tvNamaUser).text = it["nama"] as? String ?: ""
                                findViewById<TextView>(R.id.tvIdUser_isi).text = userSnapshot.key ?: "" // Use the snapshot key as ID
                                findViewById<TextView>(R.id.tvKataSandi_isi).text = it["kataSandi"] as? String ?: ""
                                findViewById<TextView>(R.id.tvNoHp_isi).text = it["nohp"] as? String ?: ""
                                findViewById<TextView>(R.id.tvCabang_isi).text = it["cabang"] as? String ?: ""

                                val role = it["role"] as? String ?: "pegawai"
                                val roleDisplay = when (role) {
                                    "admin" -> "Admin"
                                    "kepala_cabang" -> "Kepala Cabang"
                                    else -> "Pegawai"
                                }
                                findViewById<TextView>(R.id.tvPegawai).text = roleDisplay
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    override fun onBackPressed() {
        // Just call the default back behavior
        super.onBackPressed()

        // Remove the finishAffinity() line
        // finishAffinity() // This was causing the app to close completely
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            // Muat ulang data user
            val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val phoneNumber = sharedPref.getString("user_phone", "") ?: ""
            if (phoneNumber.isNotEmpty()) {
                loadUserDataFromFirebase(phoneNumber)
            }
        }
    }

    private fun logoutUser() {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}