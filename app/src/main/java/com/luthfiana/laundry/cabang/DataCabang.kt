package com.luthfiana.laundry.cabang

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
    private lateinit var rvTambahCabang: RecyclerView
    private lateinit var cabangList: ArrayList<ModelCabang>
    private lateinit var adapter: DataCabangAdapter
    private lateinit var fabTambahCabang: FloatingActionButton
    private lateinit var constraintLayout: ConstraintLayout
    private var isInAddMode = false

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
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                toggleAddMode()
            } else {
                startTambahCabangActivity()
            }
        }
    }

    private fun toggleAddMode() {
        isInAddMode = !isInAddMode

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        if (isInAddMode) {
            // Mode tambah cabang (landscape)
            rvTambahCabang.visibility = RecyclerView.VISIBLE
            // Ubah constraint rvDATA_CABANG ke 50% width
            constraintSet.connect(
                R.id.rvDATA_CABANG, ConstraintSet.END,
                R.id.guidelineVertical, ConstraintSet.START
            )
        } else {
            // Mode normal (landscape)
            rvTambahCabang.visibility = RecyclerView.GONE
            // Ubah constraint rvDATA_CABANG ke full width
            constraintSet.connect(
                R.id.rvDATA_CABANG, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        }

        constraintSet.applyTo(constraintLayout)
    }

    private fun startTambahCabangActivity() {
        val intent = Intent(this, TambahCabang::class.java).apply {
            putExtra("judul", getString(R.string.tvTambahCabang))
            putExtra("idCabang", "")
            putExtra("namaCabang", "")
            putExtra("alamatCabang", "")
            putExtra("nohpCabang", "")
        }
        startActivity(intent)
    }

    private fun initViews() {
        rvDataCabang = findViewById(R.id.rvDATA_CABANG)
        fabTambahCabang = findViewById(R.id.fabTambahCabang)
        constraintLayout = findViewById(R.id.main)

        // Hanya inisialisasi jika di landscape
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvTambahCabang = findViewById(R.id.rvTambahCabang)
            rvTambahCabang.visibility = RecyclerView.GONE
        }
    }

    private fun setupRecyclerView() {
        cabangList = ArrayList()
        adapter = DataCabangAdapter(cabangList)
        rvDataCabang.layoutManager = LinearLayoutManager(this)
        rvDataCabang.setHasFixedSize(true)
        rvDataCabang.adapter = adapter

        // Jika di landscape, setup rvTambahCabang
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvTambahCabang.layoutManager = LinearLayoutManager(this)
            // Setup adapter untuk rvTambahCabang jika diperlukan
        }
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
                    cabangList.reverse()
                    adapter.notifyDataSetChanged()
                    rvDataCabang.scrollToPosition(0)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@DataCabang, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && isInAddMode) {
            toggleAddMode()
        } else {
            super.onBackPressed()
        }
    }
}