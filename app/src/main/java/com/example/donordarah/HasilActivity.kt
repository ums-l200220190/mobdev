package com.example.donordarah

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HasilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil)

        val nama = intent.getStringExtra("NAMA")
        val status = intent.getStringExtra("STATUS")

        val txtHasil = findViewById<TextView>(R.id.txtHasil)
        val btnMaps = findViewById<Button>(R.id.btnMaps)

        txtHasil.text = "Halo $nama,\n$status"

        btnMaps.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}
