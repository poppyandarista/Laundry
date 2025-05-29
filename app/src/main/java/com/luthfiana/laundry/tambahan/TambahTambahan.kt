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
    lateinit var etStatus: EditText
    lateinit var bSimpan: Button

    var idTambahan: String=""

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
        etStatus = findViewById(R.id.etStatus)
        bSimpan = findViewById(R.id.bSimpan)
    }

    fun getData() {
        idTambahan = intent.getStringExtra("idTambahan").toString()

        val judul = intent.getStringExtra("judul")
        val namaLayananTambahan = intent.getStringExtra("namaLayananTambahan")
        val hargaTambahan = intent.getStringExtra("hargaTambahan")
        val namaCabang = intent.getStringExtra("namaCabang")
        val status = intent.getStringExtra("status")

        tvTambahTambahan.text = judul
        etNamaLayananTambahan.setText(namaLayananTambahan)
        etHarga.setText(hargaTambahan)
        etNamaCabang.setText(namaCabang)
        etStatus.setText(status)

        if (judul == "Edit Layanan Tambahan" || judul == "Edit Tambahan") {
            mati()
            bSimpan.text = "Sunting"
        } else {
            hidup()
            etNamaLayananTambahan.requestFocus()
            bSimpan.text = "Simpan"
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
        val status = etStatus.text.toString()

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
        if (status.isEmpty()) {
            etStatus.error = getString(R.string.card_tambahan_status)
            showToast(R.string.card_tambahan_status)
            etStatus.requestFocus()
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
        etNamaLayananTambahan.isEnabled=false
        etHarga.isEnabled=false
        etNamaCabang.isEnabled=false
        etStatus.isEnabled=false
    }

    fun hidup(){
        etNamaLayananTambahan.isEnabled=true
        etHarga.isEnabled=true
        etNamaCabang.isEnabled=true
        etStatus.isEnabled=true
    }

    fun update(){
        val tambahanRef = database.getReference("tambahan").child(idTambahan)

        val data = ModelTambahan(
            idTambahan,
            etNamaLayananTambahan.text.toString(),
            etHarga.text.toString(),
            etNamaCabang.text.toString(),
            etStatus.text.toString()
        )
        val updateData = mapOf(
            "namaLayananTambahan" to data.namaLayananTambahan,
            "hargaTambahan" to data.hargaTambahan,
            "namaCabang" to data.namaCabang,
            "status" to data.status
        )
        tambahanRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data layanan tambahan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnSuccessListener {
                Toast.makeText(this, "Gagal memperbarui data layanan tambahan", Toast.LENGTH_SHORT).show()
            }
    }

    fun simpan() {
        if (bSimpan.text.equals(this.getString(R.string.bsimpan))) {
            val tambahanBaru = myRef.push()
            val tambahanId = tambahanBaru.key ?: return

            val data = ModelTambahan(
                tambahanId,
                etNamaLayananTambahan.text.toString(),
                etHarga.text.toString(),
                etNamaCabang.text.toString(),
                etStatus.text.toString(),
            )

            tambahanBaru.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        this.getString(R.string.sukses_simpan_layanantambahan),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        this.getString(R.string.gagal_simpan_layanantambahan),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}
