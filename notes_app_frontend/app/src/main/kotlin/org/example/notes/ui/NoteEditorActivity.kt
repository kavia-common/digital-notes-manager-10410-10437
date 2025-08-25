package org.example.notes.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import org.example.notes.R
import org.example.notes.model.Note

/**
 * PUBLIC_INTERFACE
 * Screen for creating or editing a note with actions to save, delete and toggle pin.
 */
class NoteEditorActivity : ComponentActivity() {

    private lateinit var vm: NotesViewModel

    private var currentId: Long = 0L
    private var pinned: Boolean = false

    private lateinit var toolbar: Toolbar
    private lateinit var titleInput: TextInputEditText
    private lateinit var contentInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_editor)

        vm = ViewModelProvider(this)[NotesViewModel::class.java]

        toolbar = findViewById(R.id.editorToolbar)
        titleInput = findViewById(R.id.titleInput)
        contentInput = findViewById(R.id.contentInput)

        setActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        currentId = intent.getLongExtra(EXTRA_NOTE_ID, 0L)
        if (currentId != 0L) {
            actionBar?.title = getString(R.string.edit_note)
            vm.load(currentId) { note ->
                runOnUiThread {
                    note?.let {
                        titleInput.setText(it.title)
                        contentInput.setText(it.content)
                        pinned = it.pinned
                        updatePinIcon()
                    }
                }
            }
        } else {
            actionBar?.title = getString(R.string.new_note)
        }

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_save -> {
                    saveNoteAndFinish()
                    true
                }
                R.id.action_delete -> {
                    if (currentId != 0L) {
                        vm.delete(
                            Note(
                                currentId,
                                titleInput.text?.toString().orEmpty(),
                                contentInput.text?.toString().orEmpty(),
                                pinned
                            )
                        ) {
                            finish()
                        }
                    } else {
                        finish()
                    }
                    true
                }
                R.id.action_pin -> {
                    pinned = !pinned
                    updatePinIcon()
                    true
                }
                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updatePinIcon() {
        val menuItem = toolbar.menu.findItem(R.id.action_pin)
        menuItem.title = if (pinned) getString(R.string.unpin) else getString(R.string.pin)
        menuItem.setIcon(if (pinned) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
    }

    private fun saveNoteAndFinish() {
        val note = Note(
            id = currentId,
            title = titleInput.text?.toString().orEmpty(),
            content = contentInput.text?.toString().orEmpty(),
            pinned = pinned
        )
        vm.save(note) {
            finish()
        }
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}
