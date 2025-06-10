package com.example.donordarah

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class HasilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarHasil)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val nama = intent.getStringExtra("NAMA")
        val status = intent.getStringExtra("STATUS")
        val alasan = intent.getStringExtra("ALASAN")

        val txtNama = findViewById<TextView>(R.id.txtNama)
        val txtStatus = findViewById<TextView>(R.id.txtStatus)
        val txtAlasan = findViewById<TextView>(R.id.txtAlasan)
        val btnMaps = findViewById<Button>(R.id.btnMaps)

        txtNama.text = "\uD83D\uDC4B Halo, $nama!"

        // Gaya dan pesan berdasarkan status
        when (status) {
            "Memenuhi" -> {
                txtStatus.apply {
                    text = "✅ Anda Memenuhi Syarat untuk Donor Darah"
                    setTextColor(Color.parseColor("#388E3C")) // Hijau gelap
                }
                txtAlasan.visibility = View.GONE
                btnMaps.visibility = View.VISIBLE
            }

            "Belum Memenuhi" -> {
                txtStatus.apply {
                    text = "⚠\uFE0F Maaf, Anda Belum Memenuhi Syarat untuk Donor Darah."
                    setTextColor(Color.parseColor("#D32F2F")) // Merah
                }

                if (!alasan.isNullOrEmpty()) {
                    txtAlasan.apply {
                        text = "Karena:\n$alasan"
                        setTextColor(Color.parseColor("#F57C00")) // Oranye tua
                    }
                    txtAlasan.visibility = View.VISIBLE
                } else {
                    txtAlasan.visibility = View.GONE
                }

                btnMaps.visibility = View.GONE
            }

            else -> {
                txtStatus.apply {
                    text = "ℹ\uFE0F Status tidak diketahui. Mohon coba lagi."
                    setTextColor(Color.GRAY)
                }
                txtAlasan.visibility = View.GONE
                btnMaps.visibility = View.GONE
            }
        }

        btnMaps.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("AUTO_SHOW", true)
            startActivity(intent)
        }
    }
}
