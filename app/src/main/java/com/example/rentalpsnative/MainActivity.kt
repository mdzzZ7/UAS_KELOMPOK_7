package com.example.rentalpsnative

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var tvTotalPendapatan: TextView
    private lateinit var rentalList: ArrayList<Rental>

    // Harga Map
    private val hargaMap = mapOf("PS3" to 5000, "PS4" to 10000, "PS5" to 15000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.listView)
        tvTotalPendapatan = findViewById(R.id.tvTotalPendapatan)

        val btnTambah = findViewById<Button>(R.id.btnTambahActivity)

        btnTambah.setOnClickListener {
            val intent = Intent(this, TambahActivity::class.java)
            startActivity(intent)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedRental = rentalList[position]
            AlertDialog.Builder(this)
                .setTitle("RENTAL COMPLETED")
                .setMessage("End session for ${selectedRental.nama}?")
                .setPositiveButton("YES, END") { _, _ ->
                    dbHelper.deleteData(selectedRental.id)
                    loadData()
                    Toast.makeText(this, "Session Ended", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    fun loadData() {
        rentalList = dbHelper.getAllData()

        // Kalkulasi Total Pendapatan +
        var totalPendapatan = 0
        for (rental in rentalList) {
            val hargaSatuan = hargaMap[rental.tipe] ?: 0
            totalPendapatan += (rental.durasi * hargaSatuan)
        }

        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        tvTotalPendapatan.text = formatRupiah.format(totalPendapatan.toDouble())

        val adapter = RentalAdapter(this, R.layout.item_rental, rentalList, this)
        listView.adapter = adapter
    }

    fun tampilkanDialogPerpanjang(id: Int, nama: String) {
        val editText = EditText(this)
        editText.hint = "+ Hours"
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        val layout = LinearLayout(this)
        layout.setPadding(50, 20, 50, 0)
        layout.addView(editText)

        AlertDialog.Builder(this)
            .setTitle("Extend: $nama")
            .setView(layout)
            .setPositiveButton("UPDATE") { _, _ ->
                val input = editText.text.toString()
                if (input.isNotEmpty()) {
                    dbHelper.updateDurasi(id, input.toInt())
                    loadData()
                }
            }
            .setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}