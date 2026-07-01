package com.example.rentalpsnative

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Data Class dengan tambahan waktuMulai (Long)
data class Rental(val id: Int, val nama: String, val noHp: String, val tipe: String, val durasi: Int, val waktuMulai: Long)

// Kolom waktu_mulai
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "db_rental", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE tb_sewa (id INTEGER PRIMARY KEY AUTOINCREMENT, nama TEXT, no_hp TEXT, tipe TEXT, durasi INTEGER, waktu_mulai INTEGER)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tb_sewa")
        onCreate(db)
    }

    fun insertData(nama: String, noHp: String, tipe: String, durasi: Int, waktuMulai: Long): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("nama", nama)
        cv.put("no_hp", noHp)
        cv.put("tipe", tipe)
        cv.put("durasi", durasi)
        cv.put("waktu_mulai", waktuMulai)
        val result = db.insert("tb_sewa", null, cv)
        return result != -1L
    }

    fun getAllData(): ArrayList<Rental> {
        val rentalList = ArrayList<Rental>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM tb_sewa", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nama = cursor.getString(1)
                val noHp = cursor.getString(2)
                val tipe = cursor.getString(3)
                val durasi = cursor.getInt(4)
                val waktuMulai = cursor.getLong(5)
                rentalList.add(Rental(id, nama, noHp, tipe, durasi, waktuMulai))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return rentalList
    }

    fun updateDurasi(id: Int, tambahanJam: Int): Boolean {
        val db = this.writableDatabase
        var durasiLama = 0
        val cursor = db.rawQuery("SELECT durasi FROM tb_sewa WHERE id=?", arrayOf(id.toString()))
        if(cursor.moveToFirst()){
            durasiLama = cursor.getInt(0)
        }
        cursor.close()

        val cv = ContentValues()
        cv.put("durasi", durasiLama + tambahanJam)
        val result = db.update("tb_sewa", cv, "id=?", arrayOf(id.toString()))
        return result > 0
    }

    fun deleteData(id: Int) {
        val db = this.writableDatabase
        db.delete("tb_sewa", "id=?", arrayOf(id.toString()))
    }
}