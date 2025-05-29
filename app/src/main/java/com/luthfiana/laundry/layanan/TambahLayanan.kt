package com.luthfiana.laundry.layanan

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
import com.luthfiana.laundry.modeldata.ModelLayanan

class TambahLayanan : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")

    lateinit var tvTambahLayanan: TextView
    lateinit var etNamaLayanan: EditText
    lateinit var etCabangLayanan: EditText
    lateinit var etHargaLayanan: EditText
    lateinit var bSimpan: Button

    var idLayanan: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tambah_layanan)

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
        tvTambahLayanan = findViewById(R.id.tvTambahLayanan)
        etNamaLayanan = findViewById(R.id.etNamaLayanan)
        etCabangLayanan = findViewById(R.id.etCabangLayanan)
        etHargaLayanan = findViewById(R.id.etHargaLayanan)
        bSimpan = findViewById(R.id.bSimpan)
    }

    fun getData() {
        idLayanan = intent.getStringExtra("idLayanan").toString()

        val judul = intent.getStringExtra("judul")
        val nama = intent.getStringExtra("namaLayanan")
        val cabang = intent.getStringExtra("namaCabang")
        val harga = intent.getStringExtra("hargaLayanan")

        tvTambahLayanan.text = judul
        etNamaLayanan.setText(nama)
        etCabangLayanan.setText(cabang)
        etHargaLayanan.setText(harga)

        if (!tvTambahLayanan.text.equals(this.getString(R.string.tvTambahLayanan))) {
            if (judul.equals("Edit Layanan")) {
                mati()
                bSimpan.text = "Sunting"
            }
        }else{
            hidup()
            etNamaLayanan.requestFocus()
            bSimpan.text="Simpan"
        }
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val layananList = mutableListOf<ModelLayanan>()
                    for (dataSnapshot in snapshot.children) {
                        val layanan = dataSnapshot.getValue(ModelLayanan::class.java)
                        layanan?.let { layananList.add(it) }
                    }
                    Log.d("Data Layanan", "Data ditemukan: $layananList")
                } else {
                    Log.d("Data Layanan", "Tidak ada data layanan")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database Error", error.message)
                Toast.makeText(this@TambahLayanan, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun cekValidasi(): Boolean {
        val nama = etNamaLayanan.text.toString()
        val cabang = etCabangLayanan.text.toString()
        val harga = etHargaLayanan.text.toString()

        if (nama.isEmpty()) {
            etNamaLayanan.error = getString(R.string.card_layanan_namalayanan)
            Toast.makeText(this, getString(R.string.card_layanan_namalayanan), Toast.LENGTH_SHORT).show()
            etNamaLayanan.requestFocus()
            return false
        }
        if (cabang.isEmpty()) {
            etCabangLayanan.error = getString(R.string.nama_cabang)
            Toast.makeText(this, getString(R.string.nama_cabang), Toast.LENGTH_SHORT).show()
            etCabangLayanan.requestFocus()
            return false
        }
        if (harga.isEmpty()) {
            etHargaLayanan.error = getString(R.string.card_layanan_harga)
            Toast.makeText(this, getString(R.string.card_layanan_harga), Toast.LENGTH_SHORT).show()
            etHargaLayanan.requestFocus()
            return false
        }
        if (bSimpan.text == "Simpan") {
            simpan()
        } else if (bSimpan.text == "Sunting") {
            hidup()
            bSimpan.text = "Perbarui"
        } else if (bSimpan.text == "Perbarui") {
            update()
        }
        return true
    }

    fun mati(){
        etNamaLayanan.isEnabled=false
        etCabangLayanan.isEnabled=false
        etHargaLayanan.isEnabled=false
    }

    fun hidup(){
        etNamaLayanan.isEnabled=true
        etCabangLayanan.isEnabled=true
        etHargaLayanan.isEnabled=true
    }

    fun update (){
        val layananRef = database.getReference("layanan").child(idLayanan)

        val data = ModelLayanan(
            idLayanan,
            etNamaLayanan.text.toString(),
            etCabangLayanan.text.toString(),
            etHargaLayanan.text.toString(),
        )
    val updateData = mapOf(
        "namaLayanan" to data.namaLayanan,
        "namaCabang" to data.namaCabang,
        "hargaLayanan" to data.hargaLayanan
    )
    layananRef.updateChildren(updateData)
        .addOnSuccessListener {
            Toast.makeText(this, "Data Layanan berhasil diperbarui", Toast.LENGTH_SHORT).show()
            finish()
        }
        .addOnSuccessListener {
            Toast.makeText(this, "Gagal memperbarui data Layanan", Toast.LENGTH_SHORT).show()
        }
    }
    fun simpan() {
        if (bSimpan.text.equals(this.getString(R.string.bsimpan))) {
            val namaLayanan = etNamaLayanan.text.toString()
            val cabangLayanan = etCabangLayanan.text.toString()
            val hargaLayanan = etHargaLayanan.text.toString()

            // Buat ID unik berdasarkan nama dan cabang (atau bisa sesuai kebijakanmu)
            val layananId = myRef.push().key ?: return

            val data = ModelLayanan(
                layananId,
                namaLayanan,
                cabangLayanan,
                hargaLayanan
            )

            // Simpan langsung menggunakan ID unik sebagai key
            val layananRef = myRef.child(layananId)
            layananRef.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        getString(R.string.sukses_simpan_layanan),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        getString(R.string.gagal_simpan_layanan),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}
