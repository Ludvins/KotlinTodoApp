package com.example.kotlintodo

class Note {

    companion object {
        // Similar to a static var, used to set different IDs to each note.
        var count = 0
    }


    var id: Int? = null
    var title: String? = null
    var content: String? = null

    constructor(title: String, content: String) {
        // Every new note must be created using this.
        this.id = ++count
        this.title = title
        this.content = content
    }

    constructor(id: Int, title: String, content: String){
        // Every note created from de database must be created using this.
        this.id = id
        this.title = title
        this.content = content

        if (id > count)
            // When the program ends, count sets to 0, this is a way to recover its value when reading notes from the database at startup.
            count = id
    }

    override fun toString(): String {
        return "$title\n$content"
    }
}
