package com.luthfiana.laundry.tambahan

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luthfiana.laundry.R
import com.luthfiana.laundry.modeldata.ModelTambahan

class TambahTambahan : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")

    lateinit var tvTambahTambahan: TextView
    lateinit var etNamaLayananTambahan: EditText
    lateinit var etHarga: EditText
    lateinit var etNamaCabang: EditText
    lateinit var bSimpan: Button

    var idTambahan: String = ""
    var isEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_tambahan)

        init()
        getData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bSimpan.setOnClickListener {
            cekValidasi()
        }
    }

    fun init() {
        tvTambahTambahan = findViewById(R.id.tvTambahTambahan)
        etNamaLayananTambahan = findViewById(R.id.etNamaLayananTambahan)
        etHarga = findViewById(R.id.etHarga)
        etNamaCabang = findViewById(R.id.etNamaCabang)
        bSimpan = findViewById(R.id.bSimpan)
    }

    fun getData() {
        idTambahan = intent.getStringExtra("idTambahan").toString()

        val namaLayananTambahan = intent.getStringExtra("namaLayananTambahan")
        val hargaTambahan = intent.getStringExtra("hargaTambahan")
        val namaCabang = intent.getStringExtra("namaCabang")

        etNamaLayananTambahan.setText(namaLayananTambahan)
        etHarga.setText(hargaTambahan)
        etNamaCabang.setText(namaCabang)

        // Check if we're in edit mode by checking if idTambahan is not empty
        isEditMode = idTambahan.isNotEmpty()

        if (isEditMode) {
            tvTambahTambahan.text = getString(R.string.editlayanantambahan)
            mati()
            bSimpan.text = getString(R.string.sunting)
        } else {
            tvTambahTambahan.text = getString(R.string.tvTambahTambahan)
            hidup()
            etNamaLayananTambahan.requestFocus()
            bSimpan.text = getString(R.string.simpan)
        }

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val tambahanList = mutableListOf<ModelTambahan>()
                    for (dataSnapshot in snapshot.children) {
                        val tambahan = dataSnapshot.getValue(ModelTambahan::class.java)
                        tambahan?.let { tambahanList.add(it) }
                    }
                    Log.d("Data Layanan Tambahan", "Data ditemukan: $tambahanList")
                } else {
                    Log.d("Data Layanan Tambahan", "Tidak ada data layanan tambahan")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database Error", error.message)
                Toast.makeText(this@TambahTambahan, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun cekValidasi(): Boolean {
        val nama = etNamaLayananTambahan.text.toString()
        val harga = etHarga.text.toString()
        val namaCabang = etNamaCabang.text.toString()

        if (nama.isEmpty()) {
            etNamaLayananTambahan.error = getString(R.string.card_tambahan_namalayanan)
            showToast(R.string.card_tambahan_namalayanan)
            etNamaLayananTambahan.requestFocus()
            return false
        }
        if (harga.isEmpty()) {
            etHarga.error = getString(R.string.card_tambahan_harga)
            showToast(R.string.card_tambahan_harga)
            etHarga.requestFocus()
            return false
        }
        if (namaCabang.isEmpty()) {
            etNamaCabang.error = getString(R.string.nama_cabang)
            showToast(R.string.nama_cabang)
            etNamaCabang.requestFocus()
            return false
        }

        when (bSimpan.text.toString()) {
            getString(R.string.simpan) -> simpan()
            getString(R.string.sunting) -> {
                hidup()
                bSimpan.text = getString(R.string.perbarui)
            }
            getString(R.string.perbarui) -> update()
        }
        return true
    }

    fun mati() {
        etNamaLayananTambahan.isEnabled = false
        etHarga.isEnabled = false
        etNamaCabang.isEnabled = false
    }

    fun hidup() {
        etNamaLayananTambahan.isEnabled = true
        etHarga.isEnabled = true
        etNamaCabang.isEnabled = true
    }

    fun update() {
        val tambahanRef = database.getReference("tambahan").child(idTambahan)

        val data = ModelTambahan(
            idTambahan,
            etNamaLayananTambahan.text.toString(),
            etHarga.text.toString(),
            etNamaCabang.text.toString(),
        )
        val updateData = mapOf(
            "namaLayananTambahan" to data.namaLayananTambahan,
            "hargaTambahan" to data.hargaTambahan,
            "namaCabang" to data.namaCabang,
        )
        tambahanRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data layanan tambahan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memperbarui data layanan tambahan", Toast.LENGTH_SHORT).show()
            }
    }

    fun simpan() {
        val tambahanBaru = myRef.push()
        val tambahanId = tambahanBaru.key ?: return

        val data = ModelTambahan(
            tambahanId,
            etNamaLayananTambahan.text.toString(),
            etHarga.text.toString(),
            etNamaCabang.text.toString(),
        )

        tambahanBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    getString(R.string.sukses_simpan_layanantambahan),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    getString(R.string.gagal_simpan_layanantambahan),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}