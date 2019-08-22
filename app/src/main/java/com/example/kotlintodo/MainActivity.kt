package com.example.kotlintodo

import  android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import android.app.AlertDialog
import android.widget.*
import kotlinx.android.synthetic.main.content_main.*
import android.content.ContentValues



class MainActivity : AppCompatActivity(){

    private val notesList = mutableListOf<Note>()
    private var listAdapter: ArrayAdapter<Note>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val mainList = list_view
        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notesList)
        mainList.adapter = listAdapter

        mainList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            showEditNoteDialog(notesList[position])
        }
        fab.setOnClickListener {
            showNewNoteDialog()
        }
        loadQueryAll()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        loadQueryAll()
    }

    private fun showNewNoteDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog, null)
        dialogBuilder.setView(dialogView)

        val noteTitle = dialogView.findViewById<EditText>(R.id.title)
        val noteContent = dialogView.findViewById<EditText>(R.id.content)

        dialogBuilder.setTitle("New Task")
        dialogBuilder.setPositiveButton("Save") { _, _ ->

            notesList.add(Note(noteTitle.text.toString(), noteContent.text.toString()))

            val dbManager = NoteDbManager(this)

            val values = ContentValues()
            values.put("Title", noteTitle.text.toString())
            values.put("Content", noteContent.text.toString())
            val mID = dbManager.insert(values)

            if (mID > 0) {
                Toast.makeText(this, "Note created successfully!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to add note!", Toast.LENGTH_LONG).show()
            }
        }

        dialogBuilder.setNegativeButton("Cancel") { _, _ ->
            //pass
        }
        val b = dialogBuilder.create()
        b.show()
    }



    private fun showEditNoteDialog(note: Note) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog, null)
        dialogBuilder.setView(dialogView)

        val noteTitle = dialogView.findViewById<EditText>(R.id.title)
        val noteContent = dialogView.findViewById<EditText>(R.id.content)

        // Set default values of title and content to the actual ones.
        noteTitle.setText(note.title, TextView.BufferType.EDITABLE)
        noteContent.setText(note.content, TextView.BufferType.EDITABLE)

        dialogBuilder.setTitle("Update Task")
        dialogBuilder.setPositiveButton("Save") { _, _ ->

            // Update title and content with its fields.
            note.title = noteTitle.text.toString()
            note.content = noteContent.text.toString()

            // Update note in database
            val dbManager = NoteDbManager(this)
            val values = ContentValues()
            values.put("Title", noteTitle.text.toString())
            values.put("Content", noteContent.text.toString())

            val mID = dbManager.update(values, "ID=?", arrayOf(note.id.toString()))

            if (mID > 0) {
                Toast.makeText(this, "Note updated successfully!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to edit note!", Toast.LENGTH_LONG).show()
            }
        }

        dialogBuilder.setNegativeButton("Delete") { _, _ ->

            val dbManager = NoteDbManager(this)

            val mID = dbManager.delete("ID=?", arrayOf(note.id.toString()))

            if (mID > 0) {
                notesList.remove(note)
                listAdapter?.notifyDataSetChanged()
                Toast.makeText(this, "Note deleted successfully!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to delete note!", Toast.LENGTH_LONG).show()
            }

        }

        val b = dialogBuilder.create()
        b.show()
    }

    private fun loadQueryAll() {
        // Fills notesList with database.

        val dbManager = NoteDbManager(this)
        val cursor = dbManager.queryAll()

        notesList.clear()
        if (cursor.moveToFirst()) {

            do {
                val id = cursor.getInt(cursor.getColumnIndex("Id"))
                val title = cursor.getString(cursor.getColumnIndex("Title"))
                val content = cursor.getString(cursor.getColumnIndex("Content"))

                notesList.add(Note(id, title, content))

            } while (cursor.moveToNext())
        }
    }

}
