package com.luthfiana.laundry.tambahan

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
import com.luthfiana.laundry.adapter.DataTambahanAdapter
import com.luthfiana.laundry.modeldata.ModelTambahan

class DataTambahan : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("tambahan")
    private lateinit var rvDataTambahan: RecyclerView
    private lateinit var tambahanList: ArrayList<ModelTambahan>
    private lateinit var adapter: DataTambahanAdapter
    private lateinit var fabTambahTambahan: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_tambahan)

        initViews()
        setupRecyclerView()
        getData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabTambahTambahan.setOnClickListener {
            val intent = Intent(this@DataTambahan, TambahTambahan::class.java)
            intent.putExtra("judul", this.getString(R.string.tvTambahTambahan))
            intent.putExtra("idTambahan", "")
            intent.putExtra("namaLayananTambahan", "")
            intent.putExtra("hargaTambahan", "")
            intent.putExtra("namaCabang", "")
            startActivity(intent)

        }
    }

    private fun initViews() {
        rvDataTambahan = findViewById(R.id.rvDATA_TAMBAHAN)
        fabTambahTambahan = findViewById(R.id.fabTambahTambahan)
    }

    private fun setupRecyclerView() {
        tambahanList = ArrayList()
        adapter = DataTambahanAdapter(tambahanList)
        rvDataTambahan.layoutManager = LinearLayoutManager(this).apply {
            // Remove these if you want normal order (newest at bottom)
            // reverseLayout = true
            // stackFromEnd = true
        }
        rvDataTambahan.setHasFixedSize(true)
        rvDataTambahan.adapter = adapter
    }

    private fun getData() {
        val query = myRef.orderByChild("idTambahan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    tambahanList.clear()
                    for (childSnapshot in dataSnapshot.children) {
                        val tambahan = childSnapshot.getValue(ModelTambahan::class.java)
                        tambahan?.let { tambahanList.add(it) }
                    }
                    // Reverse the list if you want newest first
                    tambahanList.reverse()
                    adapter.notifyDataSetChanged()
                    // Scroll to top (position 0 is now the newest item)
                    rvDataTambahan.scrollToPosition(0)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@DataTambahan, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
