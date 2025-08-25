package org.example.notes.model

/**
 * PUBLIC_INTERFACE
 * Represents a note stored locally.
 */
data class Note(
    val id: Long = 0L,
    val title: String,
    val content: String,
    val pinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
