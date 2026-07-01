package com.example.rentalpsnative

import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TambahActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah)

        dbHelper = DatabaseHelper(this)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etNoHp = findViewById<EditText>(R.id.etNoHp)
        val spinnerTipe = findViewById<Spinner>(R.id.spinnerTipe)
        val etDurasi = findViewById<EditText>(R.id.etDurasi)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)

        val pilihanKonsol = arrayOf("Pilih Konsol...", "PS3", "PS4", "PS5")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, pilihanKonsol)
        spinnerTipe.adapter = adapterSpinner

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val noHp = etNoHp.text.toString().trim()
            val durasiStr = etDurasi.text.toString().trim()

            // Validasi Kolom Nama
            if (nama.isEmpty()) {
                etNama.error = "Nama Player wajib diisi!"
                etNama.requestFocus()
                return@setOnClickListener
            }

            // Validasi Kolom Nomor WhatsApp
            if (noHp.isEmpty()) {
                etNoHp.error = "Nomor WhatsApp wajib diisi!"
                etNoHp.requestFocus()
                return@setOnClickListener
            }

            // 2. Validasi Spinner Konsol
            if (spinnerTipe.selectedItemPosition == 0) {
                val errorText = spinnerTipe.selectedView as TextView
                errorText.error = "Wajib memilih konsol!"
                errorText.setTextColor(Color.RED)
                errorText.text = "Wajib memilih konsol!"
                spinnerTipe.requestFocus()
                return@setOnClickListener
            }

            // Validasi Kolom Durasi
            if (durasiStr.isEmpty()) {
                etDurasi.error = "Durasi wajib diisi!"
                etDurasi.requestFocus()
                return@setOnClickListener
            }


            val tipe = spinnerTipe.selectedItem.toString()
            val durasi = durasiStr.toInt()
            val waktuMulai = System.currentTimeMillis()

            val isInserted = dbHelper.insertData(nama, noHp, tipe, durasi, waktuMulai)
            if (isInserted) {
                Toast.makeText(this, "Session Started", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "System Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}