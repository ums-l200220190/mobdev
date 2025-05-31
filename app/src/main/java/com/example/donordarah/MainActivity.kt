package com.example.donordarah

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var edtNama: EditText
    private lateinit var edtUmur: EditText
    private lateinit var edtBerat: EditText
    private lateinit var spinnerGolongan: Spinner
    private lateinit var cbPenyakit: CheckBox
    private lateinit var btnCek: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtNama = findViewById(R.id.edtNama)
        edtUmur = findViewById(R.id.edtUmur)
        edtBerat = findViewById(R.id.edtBerat)
        spinnerGolongan = findViewById(R.id.spinnerGolongan)
        cbPenyakit = findViewById(R.id.cbPenyakit)
        btnCek = findViewById(R.id.btnCek)

        val golonganDarah = arrayOf("A", "B", "AB", "O")
        spinnerGolongan.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, golonganDarah)

        btnCek.setOnClickListener {
            val nama = edtNama.text.toString()
            val umur = edtUmur.text.toString().toIntOrNull() ?: 0
            val berat = edtBerat.text.toString().toIntOrNull() ?: 0
            val riwayat = cbPenyakit.isChecked

            val status = if (umur in 17..60 && berat >= 45 && !riwayat) {
                "Layak untuk donor darah"
            } else {
                "Belum memenuhi syarat donor"
            }

            val intent = Intent(this, HasilActivity::class.java)
            intent.putExtra("NAMA", nama)
            intent.putExtra("STATUS", status)
            startActivity(intent)
        }
    }
}
