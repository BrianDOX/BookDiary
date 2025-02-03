package com.namdo.bookdiary.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.namdo.bookdiary.data.Book
import com.namdo.bookdiary.db.BooksDao
import com.namdo.bookdiary.db.ProgressDao
import com.namdo.bookdiary.util.replaceWith
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val booksDao: BooksDao,
    private val progressDao: ProgressDao
) : ViewModel() {

    init {
        getBooks()
    }

    val books = mutableStateListOf<Book>()

    private fun getBooks() {
        viewModelScope.launch {
            booksDao.getBooks().collect {
                books.replaceWith(it)
            }
        }
    }

    fun removeBook(book: Book) {
        viewModelScope.launch {
            booksDao.deleteBook(book)
            progressDao.deleteBook(book.id)
        }
    }
}