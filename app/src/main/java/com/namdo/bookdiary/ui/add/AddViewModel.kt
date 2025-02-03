package com.namdo.bookdiary.ui.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.namdo.bookdiary.data.Book
import com.namdo.bookdiary.data.toBook
import com.namdo.bookdiary.db.BooksDao
import com.namdo.bookdiary.network.GoogleBooksService
import com.namdo.bookdiary.util.UiState
import com.namdo.bookdiary.util.networkResult
import com.namdo.bookdiary.util.replaceWith
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@HiltViewModel
class AddViewModel @Inject constructor(
    private val api: GoogleBooksService,
    private val booksDao: BooksDao
) : ViewModel() {
    var searchQuery by mutableStateOf("")
    val googleBooksSearchResult = mutableStateListOf<Book>()

    var isAddingCustomBook by mutableStateOf(false)

    private var searchJob: Job? = null


    fun searchGoogleBooks(query: String) {
        searchJob?.cancel()
        searchQuery = query
        searchJob = viewModelScope.launch {
            val result = networkResult { api.getBooks(query) }
            if (result is UiState.Success) {
                googleBooksSearchResult.replaceWith(result.data.items.map { it.toBook() })
            }
        }
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            booksDao.insertBook(book)
        }
    }

    fun clearSearchState() {
        searchQuery = ""
        googleBooksSearchResult.clear()
        isAddingCustomBook = false
        searchJob?.cancel()
        searchJob = null
    }

    fun addCustomBook(book: Book) {
        viewModelScope.launch {
            booksDao.insertBook(book.copy(
                id = book.title + book.authors.joinToString(",") + book.pageCount,
            ))
        }
    }
}