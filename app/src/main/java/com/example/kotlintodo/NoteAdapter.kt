package com.example.kotlintodo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class NoteAdapter(context: Context, private val dataSource: MutableList<Note>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val rowView = inflater.inflate(R.layout.list_item_note, parent, false)
        val titleTextView = rowView.findViewById(R.id.title) as TextView
        val contentTextView = rowView.findViewById(R.id.content) as TextView
        val note = getItem(position) as Note

        titleTextView.text = note.title
        contentTextView.text = note.content

        return rowView
    }
}


