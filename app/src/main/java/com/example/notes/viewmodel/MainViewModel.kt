package com.example.notes.viewmodel

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.notes.model.DBHelper
import com.example.notes.model.Note
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class MainViewModel(private val dbHelper: DBHelper) : ViewModel() {
    var notesList = SnapshotStateList<Note>()


    private fun getAllNotes(): List<Note> = dbHelper.getNotes()

    init {
        notesList.addAll(getAllNotes())
        sortList()
    }

    fun addNote(
        content: String,
        priority: Int
    ) {
        notesList.add(
            dbHelper.addNote(
                Note(
                    content = content,
                    priority = priority,
                    timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                        .toInstant(ZoneOffset.UTC).toEpochMilli()
                )
            )
        )
        sortList()
    }

    fun editNote(
        id: Int,
        content: String,
        priority: Int,
        timestamp: Long
    ) {
        dbHelper.updateNote(
            Note(
                id = id,
                content = content,
                priority = priority,
                timestamp = timestamp
            )
        )
        val noteIndex = notesList.indexOfFirst { it.id == id }
        val updatedNote = Note(id, content, timestamp, priority)
        notesList[noteIndex] = updatedNote
        sortList()
    }

    fun deleteNote(id: Int) {
        dbHelper.deleteNote(id)
        notesList.removeIf { it.id == id }
    }

    fun sortList() {
        val sortedNotes =
            notesList.sortedWith(compareBy<Note> { it.priority }.thenBy { it.timestamp })

        with(notesList) {
            clear()
            addAll(sortedNotes)
        }
    }
}
