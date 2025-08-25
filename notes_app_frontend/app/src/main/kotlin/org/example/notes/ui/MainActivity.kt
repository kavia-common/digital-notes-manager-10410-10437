package org.example.notes.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.example.notes.R
import org.example.notes.model.Note

/**
 * PUBLIC_INTERFACE
 * Main screen listing notes with search and FAB to add new note.
 */
class MainActivity : ComponentActivity() {

    private lateinit var vm: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vm = ViewModelProvider(this)[NotesViewModel::class.java]

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setActionBar(toolbar)

        val recycler: RecyclerView = findViewById(R.id.notesRecycler)
        val searchInput: EditText = findViewById(R.id.searchInput)
        val fab: FloatingActionButton = findViewById(R.id.addFab)
        val emptyView: TextView = findViewById(R.id.emptyView)

        val adapter = NotesAdapter { note: Note ->
            startActivity(Intent(this, NoteEditorActivity::class.java).apply {
                putExtra(NoteEditorActivity.EXTRA_NOTE_ID, note.id)
            })
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        searchInput.doAfterTextChanged { text ->
            vm.setQuery(text?.toString().orEmpty())
        }

        fab.setOnClickListener {
            startActivity(Intent(this, NoteEditorActivity::class.java))
        }

        vm.notes.observe(this) { list ->
            adapter.submitList(list)
            emptyView.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }
}
