package com.example.donordarah

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.android.material.appbar.MaterialToolbar

class RiwayatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarRiwayat)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val txtRiwayat = findViewById<TextView>(R.id.txtRiwayat)
        txtRiwayat.typeface = Typeface.MONOSPACE

        val db = AppDatabase.getDatabase(this)
        val list = db.pendonorDao().getAll()

        val builder = SpannableStringBuilder()

        if (list.isEmpty()) {
            builder.append("Belum ada data pendonor.")
        } else {
            for (it in list) {
                builder.append(boldLabel("Nama", 17)).append(": ${it.nama}\n")
                builder.append(boldLabel("Umur", 17)).append(": ${it.umur} tahun\n")
                builder.append(boldLabel("Berat", 17)).append(": ${it.berat}kg\n")
                builder.append(boldLabel("Golongan Darah", 17)).append(": ${it.golongan}\n")
                builder.append(boldLabel("Riwayat Penyakit", 17)).append(": ${if (it.riwayat) "Ya" else "Tidak"}\n")
                builder.append(boldLabel("Status", 17)).append(": ${it.status}\n\n")
            }
        }

        txtRiwayat.text = builder
    }

    private fun boldLabel(label: String, width: Int): SpannableStringBuilder {
        val padded = label.padEnd(width, ' ')
        val span = SpannableStringBuilder(padded)
        span.setSpan(StyleSpan(Typeface.BOLD), 0, padded.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }
}