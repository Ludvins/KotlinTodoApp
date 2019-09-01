package com.example.kotlintodo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val item =
            if (intent.getParcelableExtra<Note>("Note") != null)
                intent.getParcelableExtra<Note>("Note")
            else
                throw NullPointerException("Expression 'intent.getParcelableExtra<Note>(\"Note\")' must not be null")

        val noteTitle = this.findViewById<EditText>(R.id.title)
        val noteContent = this.findViewById<EditText>(R.id.content)
        noteTitle.setText(item.title)
        noteContent.setText(item.content)


        save.setOnClickListener {
            val note = Note(item.id, noteTitle.text.toString(), noteContent.text.toString())
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("Note", note)
            intent.putExtra("Action", "Save")
            setResult(Activity.RESULT_OK, intent)
            onBackPressed()
        }

        delete.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("Action", "Delete")
            setResult(Activity.RESULT_OK, intent)
            onBackPressed()
        }
    }

}
