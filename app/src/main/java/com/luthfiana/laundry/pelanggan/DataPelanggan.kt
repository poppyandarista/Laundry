package com.luthfiana.laundry.pelanggan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luthfiana.laundry.R
import com.luthfiana.laundry.adapter.DataPelangganAdapter
import com.luthfiana.laundry.modeldata.ModelPelanggan

class DataPelanggan : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pelanggan")
    private lateinit var rvDataPelanggan: RecyclerView
    private lateinit var pelangganList: ArrayList<ModelPelanggan>
    private lateinit var adapter: DataPelangganAdapter
    private lateinit var fabTambahPelanggan: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pelanggan)

        initViews()
        setupRecyclerView()
        getData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabTambahPelanggan.setOnClickListener {
            val intent = Intent(this@DataPelanggan, TambahPelanggan::class.java)
            intent.putExtra("judul", this.getString(R.string.tvtambah_pelanggan))
            intent.putExtra("idPelanggan", "")
            intent.putExtra("namaPelanggan", "")
            intent.putExtra("idCabang", "")
            intent.putExtra("alamatPelanggan", "")
            intent.putExtra("nohpPelanggan", "")
            startActivity(intent)
        }
    }

    private fun initViews() {
        rvDataPelanggan = findViewById(R.id.rvDATA_PELANGGAN)
        fabTambahPelanggan = findViewById(R.id.fabTambahPelanggan)
    }

    private fun setupRecyclerView() {
        pelangganList = ArrayList()
        adapter = DataPelangganAdapter(pelangganList)
        rvDataPelanggan.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        rvDataPelanggan.setHasFixedSize(true)
        rvDataPelanggan.adapter = adapter
    }

    private fun getData() {
        val query = myRef.orderByChild("idPelanggan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    pelangganList.clear()
                    for (childSnapshot in dataSnapshot.children) {
                        val pelanggan = childSnapshot.getValue(ModelPelanggan::class.java)
                        pelanggan?.let { pelangganList.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@DataPelanggan, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }}