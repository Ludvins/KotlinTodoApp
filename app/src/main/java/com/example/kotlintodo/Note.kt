package com.example.kotlintodo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    var id : Int,
    var title: String,
    var content : String
) : Parcelable{

    companion object {
        // Similar to a static var, used to set different IDs to each note.
        var count = 0
    }

    constructor(title: String, content: String) : this(++count, title, content)

    init {
        if (this.id > count)
            // When the program ends, count sets to 0, this is a way to recover its value when reading notes from the database at startup.
            count = id
    }

    override fun toString(): String {
        return "$title\n$content"
    }
}
