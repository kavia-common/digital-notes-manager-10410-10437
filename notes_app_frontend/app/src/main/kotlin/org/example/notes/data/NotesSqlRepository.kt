package org.example.notes.data

import android.content.ContentValues
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.notes.model.Note

/**
 * PUBLIC_INTERFACE
 * SQLite-backed repository for notes with simple in-memory LiveData updates.
 */
class NotesSqlRepository(context: Context) {

    private val helper = NotesOpenHelper(context.applicationContext)
    private val notesLive = MutableLiveData<List<Note>>(emptyList())

    init {
        // Initial load
        refresh("")
    }

    // PUBLIC_INTERFACE
    /** Live list of notes; use search() to filter. */
    fun getAllLive(): LiveData<List<Note>> = notesLive

    // PUBLIC_INTERFACE
    /** Refresh notes with optional query. */
    fun refresh(query: String) {
        val db = helper.readableDatabase
        val list = mutableListOf<Note>()
        val args = if (query.isBlank()) null else arrayOf("%$query%", "%$query%")
        val cursor = if (args == null)
            db.query("notes", null, null, null, null, null, "pinned DESC, updatedAt DESC")
        else
            db.query(
                "notes",
                null,
                "title LIKE ? OR content LIKE ?",
                args,
                null,
                null,
                "pinned DESC, updatedAt DESC"
            )
        cursor.use { c ->
            val idIdx = c.getColumnIndexOrThrow("id")
            val titleIdx = c.getColumnIndexOrThrow("title")
            val contentIdx = c.getColumnIndexOrThrow("content")
            val pinnedIdx = c.getColumnIndexOrThrow("pinned")
            val updatedIdx = c.getColumnIndexOrThrow("updatedAt")
            while (c.moveToNext()) {
                list.add(
                    Note(
                        id = c.getLong(idIdx),
                        title = c.getString(titleIdx),
                        content = c.getString(contentIdx),
                        pinned = c.getInt(pinnedIdx) == 1,
                        updatedAt = c.getLong(updatedIdx)
                    )
                )
            }
        }
        notesLive.postValue(list)
    }

    // PUBLIC_INTERFACE
    /** Insert or update a note. Returns its id. */
    suspend fun upsert(note: Note): Long = withContext(Dispatchers.IO) {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put("title", note.title)
            put("content", note.content)
            put("pinned", if (note.pinned) 1 else 0)
            put("updatedAt", System.currentTimeMillis())
        }
        val id = if (note.id == 0L) {
            db.insert("notes", null, values)
        } else {
            db.update("notes", values, "id = ?", arrayOf(note.id.toString()))
            note.id
        }
        refresh("")
        id
    }

    // PUBLIC_INTERFACE
    /** Delete a note. */
    suspend fun delete(note: Note) = withContext(Dispatchers.IO) {
        val db = helper.writableDatabase
        db.delete("notes", "id = ?", arrayOf(note.id.toString()))
        refresh("")
    }

    // PUBLIC_INTERFACE
    /** Load a single note by id. */
    suspend fun getById(id: Long): Note? = withContext(Dispatchers.IO) {
        val db = helper.readableDatabase
        val cursor = db.query("notes", null, "id = ?", arrayOf(id.toString()), null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                Note(
                    id = c.getLong(c.getColumnIndexOrThrow("id")),
                    title = c.getString(c.getColumnIndexOrThrow("title")),
                    content = c.getString(c.getColumnIndexOrThrow("content")),
                    pinned = c.getInt(c.getColumnIndexOrThrow("pinned")) == 1,
                    updatedAt = c.getLong(c.getColumnIndexOrThrow("updatedAt"))
                )
            } else null
        }
    }
}
