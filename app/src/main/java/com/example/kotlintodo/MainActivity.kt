package com.example.kotlintodo

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import android.app.AlertDialog
import android.widget.*
import kotlinx.android.synthetic.main.content_main.*
import android.content.ContentValues
import android.content.Intent
import android.view.View

class MainActivity : AppCompatActivity(){

    private val NEW_NOTE_CODE = 1
    private val EDIT_NOTE_CODE = 2
    private val notesList = mutableListOf<Note>()
    private var listAdapter: NoteAdapter? = null
    private var currentNote: Note? = null

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val mainList = list_view
        listAdapter = NoteAdapter(this, notesList)
        mainList.adapter = listAdapter

        mainList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            currentNote = notesList[position]
            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra("Note", currentNote)
            startActivityForResult(intent, EDIT_NOTE_CODE)
        }
        fab.setOnClickListener {
              quickNoteDialog()
        }

        this.new_note_button.setOnClickListener {
            currentNote = Note("", "")

            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra("Note", currentNote)
            startActivityForResult(intent, EDIT_NOTE_CODE)
        }

        loadQueryAll()
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check which request we're responding to
        if (requestCode == EDIT_NOTE_CODE) {
            if (resultCode == Activity.RESULT_OK && currentNote != null ){

                when (data?.getStringExtra("Action")) {
                     "Save" -> {
                         val note = data.getParcelableExtra<Note>("Note")!!

                         if (notesList.find { it.id == currentNote!!.id } != null) {
                             editNoteOnDatabaseAndList(currentNote!!, note)
                         } else {
                             addNoteToDatabaseAndList(note)
                         }

                         currentNote = null
                    }
                    "Delete" -> {
                        deleteNoteFromDatabaseAndList(currentNote!!)
                        currentNote = null
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_clear_list -> {
                notesList.clear()
                val dbManager = NoteDbManager(this)
                dbManager.deleteAll()
                listAdapter?.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        loadQueryAll()
    }

    // ----------------------------------------------------------------
    // --------------------------- DIALOGS ----------------------------
    // ----------------------------------------------------------------

    private fun quickNoteDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = View.inflate(this, R.layout.note_dialog, null)
        dialogBuilder.setView(dialogView)

        val noteTitle = dialogView.findViewById<EditText>(R.id.title)
        val noteContent = dialogView.findViewById<EditText>(R.id.content)

        dialogBuilder.setTitle("New Task")
        dialogBuilder.setPositiveButton("Save") { _, _ ->

            val note = Note(noteTitle.text.toString(), noteContent.text.toString())
            addNoteToDatabaseAndList(note)

        }

        dialogBuilder.setNegativeButton("Cancel") { _, _ ->
            //pass
        }
        val b = dialogBuilder.create()
        b.show()
    }

    // ----------------------------------------------------------------
    // ------------------ DATABASE METHODS ----------------------------
    // ----------------------------------------------------------------

    private fun addNoteToDatabaseAndList(note: Note){
        // Update note in database

        val dbManager = NoteDbManager(this)
        val values = ContentValues()
        values.put("Title", note.title)
        values.put("Content", note.content)

        val mID = dbManager.insert(values)

        if (mID > 0) {
            notesList.add(note)
            listAdapter?.notifyDataSetChanged()
            Toast.makeText(this, "Note created successfully!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Failed to add note!", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteNoteFromDatabaseAndList(note: Note) {

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

    private fun editNoteOnDatabaseAndList(old: Note, new: Note){

        assert(old.id == new.id)

        // Update note in database
        val dbManager = NoteDbManager(this)
        val values = ContentValues()
        values.put("Title", new.title)
        values.put("Content", new.content)

        val mID = dbManager.update(values, "ID=?", arrayOf(old.id.toString()))
        if (mID > 0) {
            notesList[notesList.indexOf(old)] = new
            listAdapter?.notifyDataSetChanged()
            Toast.makeText(this, "Note updated successfully!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Failed to edit note!", Toast.LENGTH_LONG).show()
        }
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
