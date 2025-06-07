package com.example.donordarah

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    private lateinit var edtNama: EditText
    private lateinit var edtUmur: EditText
    private lateinit var edtBerat: EditText
    private lateinit var spinnerGolongan: Spinner
    private lateinit var cbPenyakit: CheckBox
    private lateinit var btnCek: Button
    // Database - 5/6/2025
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarMain)
        setSupportActionBar(toolbar)

        edtNama = findViewById(R.id.edtNama)
        edtUmur = findViewById(R.id.edtUmur)
        edtBerat = findViewById(R.id.edtBerat)
        spinnerGolongan = findViewById(R.id.spinnerGolongan)
        cbPenyakit = findViewById(R.id.cbPenyakit)
        btnCek = findViewById(R.id.btnCek)

        val golonganDarah = arrayOf("A", "B", "AB", "O")
        spinnerGolongan.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, golonganDarah)

        // Database - 5/6/2025
        db = AppDatabase.getDatabase(this)

        btnCek.setOnClickListener {
            val nama = edtNama.text.toString().trim()
            val umurText = edtUmur.text.toString().trim()
            val beratText = edtBerat.text.toString().trim()

            when {
                nama.isEmpty() -> {
                    Toast.makeText(this, "Nama Harus Diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                umurText.isEmpty() -> {
                    Toast.makeText(this, "Umur Harus Diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                beratText.isEmpty() -> {
                    Toast.makeText(this, "Berat Badan Harus Diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val umur = umurText.toIntOrNull() ?: 0
            val berat = beratText.toIntOrNull() ?: 0
            val riwayat = cbPenyakit.isChecked

            val status = if (umur in 17..60 && berat >= 45 && !riwayat) {
                "Memenuhi"
            } else {
                "Belum Memenuhi"
            }

            val golongan = spinnerGolongan.selectedItem.toString()

            val pendonor = Pendonor(
                nama = nama,
                umur = umur,
                berat = berat,
                golongan = golongan,
                riwayat = riwayat,
                status = status,
                waktu = System.currentTimeMillis()
            )

            db.pendonorDao().insert(pendonor)

            val intent = Intent(this, HasilActivity::class.java)
            intent.putExtra("NAMA", nama)
            intent.putExtra("STATUS", status)
            startActivity(intent)
        }
    }

    // Inflate menu ke toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Tangani klik tombol menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_riwayat -> {
                val intent = Intent(this, RiwayatActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
