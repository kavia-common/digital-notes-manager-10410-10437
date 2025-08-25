package org.example.notes.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * PUBLIC_INTERFACE
 * SQLiteOpenHelper for Notes database.
 */
class NotesOpenHelper(context: Context) : SQLiteOpenHelper(context, "notes_db.sqlite", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE notes(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                pinned INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_notes_pinned_updated ON notes(pinned DESC, updatedAt DESC)")
        db.execSQL("CREATE INDEX idx_notes_search ON notes(title, content)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // For this simple app, drop and recreate
        db.execSQL("DROP TABLE IF EXISTS notes")
        onCreate(db)
    }
}
