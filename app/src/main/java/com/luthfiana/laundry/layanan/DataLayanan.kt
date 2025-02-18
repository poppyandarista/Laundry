package com.luthfiana.laundry.layanan

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
import com.luthfiana.laundry.adapter.DataLayananAdapter
import com.luthfiana.laundry.adapter.DataPelangganAdapter
import com.luthfiana.laundry.modeldata.ModelLayanan
import com.luthfiana.laundry.modeldata.ModelPelanggan

class DataLayanan : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("layanan")
    private lateinit var rvDataLayanan: RecyclerView
    private lateinit var layananList: ArrayList<ModelLayanan>
    private lateinit var fabTambahLayanan: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_layanan)

       initViews()
        setupRecyclerView()
        getData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fabTambahLayanan.setOnClickListener {
            val intent = Intent(this@DataLayanan, TambahLayanan::class.java)
            startActivity(intent)
        }
    }
    private fun initViews() {
        rvDataLayanan = findViewById(R.id.rvDATA_LAYANAN)
        fabTambahLayanan = findViewById(R.id.fabTambahLayanan)
    }
    private fun setupRecyclerView() {
        rvDataLayanan.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        rvDataLayanan.setHasFixedSize(true)
        layananList = ArrayList()
    }
    private fun getData() {
        val query = myRef.orderByChild("idLayanan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    layananList.clear()
                    for (childSnapshot in dataSnapshot.children) {
                        val layanan = childSnapshot.getValue(ModelLayanan::class.java)
                        layanan?.let { layananList.add(it) }
                    }
                    rvDataLayanan.adapter = DataLayananAdapter(layananList).also {
                        it.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@DataLayanan, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}