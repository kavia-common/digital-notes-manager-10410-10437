package org.example.notes.ui

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.notes.data.NotesSqlRepository
import org.example.notes.model.Note

/**
 * PUBLIC_INTERFACE
 * ViewModel managing the list of notes and CRUD operations.
 */
class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotesSqlRepository(application)

    private val queryLive = MutableLiveData("")

    val notes: LiveData<List<Note>> = MediatorLiveData<List<Note>>().also { mediator ->
        mediator.addSource(repository.getAllLive()) { list ->
            mediator.value = applyFilter(list, queryLive.value.orEmpty())
        }
        mediator.addSource(queryLive) { q ->
            mediator.value = applyFilter(repository.getAllLive().value.orEmpty(), q)
        }
    }

    private fun applyFilter(list: List<Note>, q: String): List<Note> {
        if (q.isBlank()) return list
        val s = q.trim().lowercase()
        return list.filter { it.title.lowercase().contains(s) || it.content.lowercase().contains(s) }
    }

    // PUBLIC_INTERFACE
    /** Sets current search query and triggers filtering. */
    fun setQuery(q: String) {
        queryLive.value = q
    }

    // PUBLIC_INTERFACE
    /** Saves a note (insert or update). Returns the id via callback. */
    fun save(note: Note, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.upsert(note.copy(updatedAt = System.currentTimeMillis()))
            onSaved(id)
        }
    }

    // PUBLIC_INTERFACE
    /** Deletes a note. */
    fun delete(note: Note, onDone: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(note)
            onDone()
        }
    }

    // PUBLIC_INTERFACE
    /** Loads a note by id. */
    fun load(id: Long, onLoaded: (Note?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val n = repository.getById(id)
            onLoaded(n)
        }
    }
}
