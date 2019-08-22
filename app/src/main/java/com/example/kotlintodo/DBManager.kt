package com.example.kotlintodo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class NoteDbManager(context: Context) {

    private val dbName = "Todo"
    private val dbTable = "Notes"
    private val colId = "Id"
    private val colTitle = "Title"
    private val colContent = "Content"
    private val dbVersion = 1

    private val CREATE_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS $dbTable ($colId INTEGER PRIMARY KEY,$colTitle TEXT, $colContent TEXT);"
    private var db: SQLiteDatabase? = null

    init {
        val dbHelper = DatabaseHelper(context)
        db = dbHelper.writableDatabase
    }

    fun insert(values: ContentValues): Long {

        val ID = db!!.insert(dbTable, "", values)
        return ID
    }

    fun queryAll(): Cursor {

        return db!!.rawQuery("select * from $dbTable", null)
    }

    fun delete(selection: String, selectionArgs: Array<String>): Int {

        return db!!.delete(dbTable, selection, selectionArgs)
    }

    fun update(values: ContentValues, selection: String, selectionargs: Array<String>): Int {

        return db!!.update(dbTable, values, selection, selectionargs)
    }

    inner class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, dbName, null, dbVersion) {

        private var context: Context? = context

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(CREATE_TABLE_SQL)
            Toast.makeText(this.context, " database is created", Toast.LENGTH_LONG).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop table IF EXISTS $dbTable")
        }
    }
}