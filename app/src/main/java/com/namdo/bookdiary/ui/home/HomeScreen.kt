package com.namdo.bookdiary.ui.home

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namdo.bookdiary.data.Book
import com.namdo.bookdiary.ui.components.BookRow
import com.namdo.bookdiary.ui.components.EmptyLottie

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onBookClicked: (String) -> Unit
) {

    var searchQuery by remember { mutableStateOf("") }
    val filteredBooks by remember(searchQuery, viewModel.books) {
        derivedStateOf {
            viewModel.books.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }
    }

    val finishedBooks by remember(filteredBooks) { derivedStateOf { filteredBooks.filter { it.currentPage == it.pageCount } } }
    val currentlyReadingBooks by remember(filteredBooks) { derivedStateOf { filteredBooks.filter { it.currentPage > 1 && it.currentPage < it.pageCount } } }
    val notStartedBooks by remember(filteredBooks) { derivedStateOf { filteredBooks.filter { it.currentPage == 1 } } }

    fun LazyListScope.bookRow(books: List<Book>) {
        items(books, key = { it.id }) {
            BookRow(
                book = it,
                isFromAddScreen = false,
                onDeleteClicked = { viewModel.removeBook(it) },
                onClick = { onBookClicked(it.id) }
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it }, // Cập nhật chuỗi tìm kiếm
                label = { Text("Search Books") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )
        }
        if (filteredBooks.isEmpty()) {
            item {
                EmptyLottie()
            }
        }
        if (currentlyReadingBooks.isNotEmpty()) {
            item {
                SectionHeader("Currently Reading")
            }
            bookRow(currentlyReadingBooks)
        }
        if (notStartedBooks.isNotEmpty()) {
            item {
                SectionHeader("Not Started Yet")
            }
            bookRow(notStartedBooks)
        }
        if (finishedBooks.isNotEmpty()) {
            item {
                SectionHeader("Finished")
            }
            bookRow(finishedBooks)
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.h5,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}
