package com.example.happyplaces.module.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.happyplaces.module.models.HappyPlaceModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "HappyPlacesDatabase"
        private const val TABLE_NAME = "HappyPlacesTable"

        // Table column names
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$KEY_TITLE TEXT," +
                "$KEY_IMAGE TEXT," +
                "$KEY_DESCRIPTION TEXT," +
                "$KEY_DATE TEXT," +
                "$KEY_LOCATION TEXT," +
                "$KEY_LATITUDE REAL," +
                "$KEY_LONGITUDE REAL" +
                ")"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addHappyPlace(happyPlace: HappyPlaceModel): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_TITLE, happyPlace.title)
        values.put(KEY_IMAGE, happyPlace.image)
        values.put(KEY_DESCRIPTION, happyPlace.description)
        values.put(KEY_DATE, happyPlace.date)
        values.put(KEY_LOCATION, happyPlace.location)
        values.put(KEY_LATITUDE, happyPlace.latitude)
        values.put(KEY_LONGITUDE, happyPlace.longitude)

        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getHappyPlace(id: Int): HappyPlaceModel? {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            null,
            "$KEY_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val happyPlace = HappyPlaceModel(
                cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
            )
            cursor.close()
            happyPlace
        } else {
            cursor.close()
            null
        }
    }

    @SuppressLint("Range")
    fun getAllHappyPlaces(): List<HappyPlaceModel> {
        val happyPlacesList = ArrayList<HappyPlaceModel>()
        val db = this.readableDatabase
        val selectAllQuery = "SELECT * FROM $TABLE_NAME"
        val cursor: Cursor = db.rawQuery(selectAllQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val happyPlace = HappyPlaceModel(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                    cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                    cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                )
                happyPlacesList.add(happyPlace)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return happyPlacesList
    }

    fun updateHappyPlace(happyPlace: HappyPlaceModel): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_TITLE, happyPlace.title)
        values.put(KEY_IMAGE, happyPlace.image)
        values.put(KEY_DESCRIPTION, happyPlace.description)
        values.put(KEY_DATE, happyPlace.date)
        values.put(KEY_LOCATION, happyPlace.location)
        values.put(KEY_LATITUDE, happyPlace.latitude)
        values.put(KEY_LONGITUDE, happyPlace.longitude)

        return db.update(
            TABLE_NAME,
            values,
            "$KEY_ID = ?",
            arrayOf(happyPlace.id.toString())
        )
    }

    fun deleteHappyPlace(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$KEY_ID=?", arrayOf(id.toString()))
    }
}