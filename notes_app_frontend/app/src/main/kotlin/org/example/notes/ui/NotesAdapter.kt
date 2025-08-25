package org.example.notes.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.example.notes.R
import org.example.notes.model.Note
import java.text.DateFormat
import java.util.Date

/**
 * PUBLIC_INTERFACE
 * Adapter for the notes list.
 */
class NotesAdapter(
    private val onClick: (Note) -> Unit
) : ListAdapter<Note, NotesAdapter.NoteVH>(NoteDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteVH(v, onClick)
    }

    override fun onBindViewHolder(holder: NoteVH, position: Int) {
        holder.bind(getItem(position))
    }

    class NoteVH(itemView: View, private val onClick: (Note) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.titleText)
        private val content: TextView = itemView.findViewById(R.id.contentPreview)
        private val date: TextView = itemView.findViewById(R.id.dateText)
        private val pin: ImageView = itemView.findViewById(R.id.pinIcon)

        fun bind(note: Note) {
            title.text = note.title.ifBlank { "(No title)" }
            content.text = note.content
            date.text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                .format(Date(note.updatedAt))
            pin.visibility = if (note.pinned) View.VISIBLE else View.GONE
            itemView.setOnClickListener { onClick(note) }
        }
    }

    companion object {
        private object NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem
        }
    }
}
