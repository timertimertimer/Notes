package com.example.notes.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(
        context,
        "my_db",
        null,
        1
    ) {

    object NoteEntry {
        const val TABLE_NAME = "notes"
        const val ID = "id"
        const val CONTENT = "content"
        const val TIMESTAMP = "timestamp"
        const val PRIORITY = "priority"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.let { b ->
            b.beginTransaction()
            b.execSQL(
                """
                CREATE TABLE ${NoteEntry.TABLE_NAME} (
                    ${NoteEntry.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                    ${NoteEntry.CONTENT} TEXT,
                    ${NoteEntry.TIMESTAMP} INTEGER NOT NULL,
                    ${NoteEntry.PRIORITY} INTEGER NOT NULL
                )
            """.trimIndent()
            )
            b.setTransactionSuccessful()
            b.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    private fun createSelection(noteTemp: Note?): String? {
        noteTemp ?: return null

        return listOfNotNull(
            noteTemp.id?.let { "${NoteEntry.ID} = ?" },
            noteTemp.content.let { "${NoteEntry.CONTENT} = ?" },
            noteTemp.timestamp.let { "${NoteEntry.TIMESTAMP} = ?" },
            noteTemp.priority.let { "${NoteEntry.PRIORITY} = ?" }
        ).joinToString(" AND ")
    }

    private fun createSelectionArgs(noteTemp: Note?): Array<String>? {
        noteTemp ?: return null

        return listOfNotNull(
            noteTemp.id?.toString(),
            noteTemp.content,
            noteTemp.timestamp.toString(),
            noteTemp.priority.toString()
        ).toTypedArray()
    }

    @SuppressLint("Range")
    fun getNotes(noteTemp: Note? = null): List<Note> {
        val lst = mutableListOf<Note>()
        val selection = createSelection(noteTemp)
        val selectionArgs = createSelectionArgs(noteTemp)
        with(readableDatabase) {
            query(
                NoteEntry.TABLE_NAME,
                arrayOf(), // Пустой массив возвращает все столбцы.
                selection,
                selectionArgs,
                null,
                null,
                null
            ).let { cursor ->
                while (cursor.moveToNext()) {
                    lst.add(
                        Note(
                            id = cursor.getInt(cursor.getColumnIndex(NoteEntry.ID)),
                            content = cursor.getString(cursor.getColumnIndex(NoteEntry.CONTENT)),
                            timestamp = cursor.getLong(cursor.getColumnIndex(NoteEntry.TIMESTAMP)),
                            priority = cursor.getInt(cursor.getColumnIndex(NoteEntry.PRIORITY))
                        )
                    )
                }
                cursor.close()
            }
        }
        return lst
    }

    fun addNote(note: Note) : Note {
        val cv = ContentValues().apply {
            put(NoteEntry.CONTENT, note.content)
            put(NoteEntry.TIMESTAMP, note.timestamp)
            put(NoteEntry.PRIORITY, note.priority)
        }
        with(writableDatabase) {
            beginTransaction()
            insert(NoteEntry.TABLE_NAME, null, cv)
            setTransactionSuccessful()
            endTransaction()
        }
        return getNotes(note).last()
    }

    fun deleteNote(id: Int) {
        with(writableDatabase) {
            delete(NoteEntry.TABLE_NAME, "${NoteEntry.ID} = ?", arrayOf(id.toString()))
        }
    }

    fun updateNote(note: Note) {
        val cv = ContentValues().apply {
            put(NoteEntry.CONTENT, note.content)
            put(NoteEntry.TIMESTAMP, note.timestamp)
            put(NoteEntry.PRIORITY, note.priority)
        }
        with(writableDatabase) {
            beginTransaction()
            update(
                NoteEntry.TABLE_NAME,
                cv,
                "${NoteEntry.ID} = ?",
                arrayOf(note.id.toString())
            )
            setTransactionSuccessful()
            endTransaction()
        }
    }
}