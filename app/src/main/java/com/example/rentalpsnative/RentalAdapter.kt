package com.example.rentalpsnative

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RentalAdapter(
    context: Context,
    private val resource: Int,
    private val objects: List<Rental>,
    private val mainActivity: MainActivity
) : ArrayAdapter<Rental>(context, resource, objects) {

    // Harga dinamis berdasarkan konsol
    private val hargaMap = mapOf("PS3" to 5000, "PS4" to 10000, "PS5" to 15000)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(resource, parent, false)

        val tvNama = view.findViewById<TextView>(R.id.tvItemNama)
        val tvHargaDetail = view.findViewById<TextView>(R.id.tvHargaDetail)
        val tvJamSelesai = view.findViewById<TextView>(R.id.tvJamSelesai)
        val btnWA = view.findViewById<Button>(R.id.btnWA)
        val btnPerpanjang = view.findViewById<Button>(R.id.btnPerpanjang)

        val rental = objects[position]

        tvNama.text = rental.nama.uppercase()

        // Kalkulasi Harga Tampil di Kolom Pelanggan
        val hargaPerJam = hargaMap[rental.tipe] ?: 0
        val totalHarga = hargaPerJam * rental.durasi
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val teksTotal = formatRupiah.format(totalHarga.toDouble())
        tvHargaDetail.text = "${rental.tipe}  •  ${rental.durasi} Jam  •  $teksTotal"

        // Kalkulasi Jam Selesai Penyewaan (Realtime)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = rental.waktuMulai
        calendar.add(Calendar.HOUR_OF_DAY, rental.durasi) // Menambah jam
        val formatJam = SimpleDateFormat("HH:mm 'WIB'", Locale.getDefault())
        tvJamSelesai.text = "End: ${formatJam.format(calendar.time)}"

        btnWA.setOnClickListener {
            var formattedHp = rental.noHp
            if (formattedHp.startsWith("08")) {
                formattedHp = "628" + formattedHp.substring(2)
            }
            val url = "https://api.whatsapp.com/send?phone=$formattedHp"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        }

        btnPerpanjang.setOnClickListener {
            mainActivity.tampilkanDialogPerpanjang(rental.id, rental.nama)
        }

        return view
    }
}