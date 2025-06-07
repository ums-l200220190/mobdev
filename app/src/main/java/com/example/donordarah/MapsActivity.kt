package com.example.donordarah

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.MaterialToolbar

class MapsActivity : AppCompatActivity() {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val GPS_REQUEST_CODE = 2001
    private var lokasiPMITerdekat: LatLng? = null

    // Daftar PMI
    private val daftarPMI = listOf(
        Pair(LatLng(-7.5597647,110.8430928), "PMI Kota Surakarta"),
        Pair(LatLng(-7.6933244,110.8367378), "PMI Kab Sukoharjo"),
        Pair(LatLng(-7.6944167,110.6098714), "PMI Klaten Kota"),
        Pair(LatLng(-7.5378536,110.6009275), "PMI Kab Boyolali"),
        Pair(LatLng(-7.8015039,110.9151525), "PMI Wonogiri"),
        Pair(LatLng(-7.8261016,110.3920925), "PMI Yogyakarta")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarMap)
        setSupportActionBar(toolbar)

        // Aktifkan tombol back di toolbar
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync {
            map = it

            // Set default camera ke Jakarta
            val defaultLocation = LatLng(-6.2088, 106.8456) // Koordinat Jakarta
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        }

        // Tombol buka di Google Maps
        val btnBukaGoogleMaps = findViewById<Button>(R.id.btnBukaGoogleMaps)
        btnBukaGoogleMaps.setOnClickListener {
            lokasiPMITerdekat?.let {
                val destination = "${it.latitude},${it.longitude}"
                val gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$destination&travelmode=driving")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(this, "Google Maps tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "Lokasi PMI belum ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol tampilkan lokasi PMI
        val btnTampilkanPMI = findViewById<Button>(R.id.btnTampilkanPMI)
        btnTampilkanPMI.setOnClickListener {
            cekGPSdanMintaAktifkan()
        }

        // Jika dikirim intent AUTO_SHOW dari HasilActivity
        if (intent.getBooleanExtra("AUTO_SHOW", false)) {
            cekGPSdanMintaAktifkan()
        }
    }

    private fun cekGPSdanMintaAktifkan() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<com.google.android.gms.location.LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // GPS sudah aktif, lanjutkan ambil lokasi
            enableMyLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        this@MapsActivity,
                        2001 // requestCode bisa sembarang
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Gagal membuka pengaturan lokasi
                    Toast.makeText(this, "Gagal membuka pengaturan lokasi", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Pengaturan lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions().position(userLatLng).title("Lokasi Anda"))

                    // Cari PMI terdekat
                    val pmiTerdekat = cariPMITerdekat(location)
                    lokasiPMITerdekat = pmiTerdekat

                    val namaPMI = daftarPMI.find { it.first == pmiTerdekat }?.second ?: "PMI Terdekat"
                    map.addMarker(
                        MarkerOptions()
                            .position(pmiTerdekat)
                            .title(namaPMI)
                    )

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 10f))

                    val results = FloatArray(1)
                    Location.distanceBetween(
                        location.latitude, location.longitude,
                        pmiTerdekat.latitude, pmiTerdekat.longitude,
                        results
                    )
                    val jarak = results[0]
                    Toast.makeText(
                        this,
                        "Jarak ke PMI terdekat: ${"%.2f".format(jarak / 1000)} km",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun cariPMITerdekat(lokasiPengguna: Location): LatLng {
        var lokasiTerdekat = daftarPMI[0].first
        var jarakTerdekat = Float.MAX_VALUE

        for ((pmi, _) in daftarPMI) {
            val results = FloatArray(1)
            Location.distanceBetween(
                lokasiPengguna.latitude, lokasiPengguna.longitude,
                pmi.latitude, pmi.longitude,
                results
            )
            if (results[0] < jarakTerdekat) {
                jarakTerdekat = results[0]
                lokasiTerdekat = pmi
            }
        }
        return lokasiTerdekat
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                cekGPSdanMintaAktifkan()
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GPS_REQUEST_CODE) {
            // Coba aktifkan lokasi lagi setelah user menyalakan GPS
            enableMyLocation()
        }
    }
}
