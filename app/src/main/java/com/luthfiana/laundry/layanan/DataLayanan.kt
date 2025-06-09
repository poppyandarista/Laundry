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
import com.google.firebase.database.*
import com.luthfiana.laundry.R
import com.luthfiana.laundry.adapter.DataLayananAdapter
import com.luthfiana.laundry.adapter.DataPelangganAdapter
import com.luthfiana.laundry.modeldata.ModelLayanan

class DataLayanan : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("layanan")
    private lateinit var rvDataLayanan: RecyclerView
    private lateinit var layananList: ArrayList<ModelLayanan>
    private lateinit var adapter: DataLayananAdapter
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
            intent.putExtra("judul", getString(R.string.tvTambahLayanan))
            intent.putExtra("idLayanan", "")
            intent.putExtra("namaLayanan", "")
            intent.putExtra("namaCabang", "")
            intent.putExtra("hargaLayanan", "")
            startActivity(intent)
        }
    }

    private fun initViews() {
        rvDataLayanan = findViewById(R.id.rvDATA_LAYANAN)
        fabTambahLayanan = findViewById(R.id.fabTambahLayanan)
    }

    private fun setupRecyclerView() {
        layananList = ArrayList()
        adapter = DataLayananAdapter(layananList)
        rvDataLayanan.layoutManager = LinearLayoutManager(this).apply {
            // Remove these if you want normal order (newest at bottom)
            // reverseLayout = true
            // stackFromEnd = true
        }
        rvDataLayanan.setHasFixedSize(true)
        rvDataLayanan.adapter = adapter
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
                    layananList.reverse()
                    adapter.notifyDataSetChanged()
                    // Scroll to top (position 0 is now the newest item)
                    rvDataLayanan.scrollToPosition(0)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@DataLayanan, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
