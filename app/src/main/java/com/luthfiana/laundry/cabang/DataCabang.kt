package com.luthfiana.laundry.cabang

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
import com.luthfiana.laundry.adapter.DataCabangAdapter
import com.luthfiana.laundry.modeldata.ModelCabang

class DataCabang : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("cabang")
    private lateinit var rvDataCabang: RecyclerView
    private lateinit var cabangList: ArrayList<ModelCabang>
    private lateinit var adapter: DataCabangAdapter
    private lateinit var fabTambahCabang: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_cabang)

        initViews()
        setupRecyclerView()
        getData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabTambahCabang.setOnClickListener {
            val intent = Intent(this@DataCabang, TambahCabang::class.java)
            intent.putExtra("judul", this.getString(R.string.tvTambahCabang))
            intent.putExtra("idCabang", "")
            intent.putExtra("namaCabang", "")
            intent.putExtra("alamatCabang", "")
            intent.putExtra("nohpCabang", "")
            startActivity(intent)
             }
    }

    private fun initViews() {
        rvDataCabang = findViewById(R.id.rvDATA_CABANG)
        fabTambahCabang = findViewById(R.id.fabTambahCabang)
    }

    private fun setupRecyclerView() {
        rvDataCabang.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        rvDataCabang.setHasFixedSize(true)
        cabangList = ArrayList()
    }

    private fun getData() {
        val query = myRef.orderByChild("idCabang").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    cabangList.clear()
                    for (childSnapshot in dataSnapshot.children) {
                        val cabang = childSnapshot.getValue(ModelCabang::class.java)
                        cabang?.let { cabangList.add(it) }
                    }
                    rvDataCabang.adapter = DataCabangAdapter(cabangList).also {
                        it.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@DataCabang, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
