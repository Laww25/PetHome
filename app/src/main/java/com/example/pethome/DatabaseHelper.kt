package com.example.pethome

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "pethome.db"
        private const val DATABASE_VERSION = 1

        // Tabla
        const val TABLE_MASCOTAS = "mascotas"

        // Columnas
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_TIPO = "tipo"
        const val COL_RAZA = "raza"
        const val COL_EDAD = "edad"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_MASCOTAS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_TIPO TEXT NOT NULL,
                $COL_RAZA TEXT NOT NULL,
                $COL_EDAD INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MASCOTAS")
        onCreate(db)
    }

    //  CRUD

    fun insertMascota(nombre: String, tipo: String, raza: String, edad: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_TIPO, tipo)
            put(COL_RAZA, raza)
            put(COL_EDAD, edad)
        }
        val id = db.insert(TABLE_MASCOTAS, null, values)
        db.close()
        return id
    }

    fun getMascotas(): List<Mascota> {
        val lista = mutableListOf<Mascota>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT $COL_ID, $COL_NOMBRE, $COL_TIPO, $COL_RAZA, $COL_EDAD FROM $TABLE_MASCOTAS ORDER BY $COL_ID DESC",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val tipo = cursor.getString(2)
                val raza = cursor.getString(3)
                val edad = cursor.getInt(4)

                // Ajusta si tu modelo Mascota no tiene id (te digo abajo qué hacer)
                lista.add(Mascota(id, nombre, tipo, raza, edad))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun deleteMascota(id: Int): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_MASCOTAS, "$COL_ID=?", arrayOf(id.toString()))
        db.close()
        return rows
    }
    fun updateMascota(id: Int, nombre: String, tipo: String, raza: String, edad: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_TIPO, tipo)
            put(COL_RAZA, raza)
            put(COL_EDAD, edad)
        }
        val rows = db.update(TABLE_MASCOTAS, values, "$COL_ID=?", arrayOf(id.toString()))
        db.close()
        return rows
    }
}