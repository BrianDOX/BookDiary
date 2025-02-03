package com.namdo.bookdiary.ui.detail

import android.net.Uri
import java.io.File
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.namdo.bookdiary.R
import com.namdo.bookdiary.ui.charty.common.axis.AxisConfig
import com.namdo.bookdiary.ui.charty.line.LineChart
import com.namdo.bookdiary.ui.charty.line.model.LineData
import com.namdo.bookdiary.data.Book
import com.namdo.bookdiary.data.Progress
import com.namdo.bookdiary.data.minutesReadForLastSevenDays
import com.namdo.bookdiary.data.pagesReadForLastSevenDays
import com.namdo.bookdiary.data.progress
import com.namdo.bookdiary.data.publishingDetails
import com.namdo.bookdiary.util.clickWithRipple

@Composable
fun DetailScreen(
    bookId: String,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    LaunchedEffect(bookId) {
        viewModel.getBook(bookId)
    }

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) PageDialog(
        initialCurrentPage = viewModel.book.currentPage,
        initialTotalPages = viewModel.book.pageCount
    ) { current, total, minutes ->
        showDialog = false
        viewModel.updateProgress(bookId, current, total, minutes)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(viewModel.book)
        Spacer(Modifier.height(16.dp))
        ReadCountButton(viewModel.book) { showDialog = true }
        Spacer(Modifier.height(16.dp))
        AdditionalDetails(viewModel.book)
        Graphs(viewModel.progress)
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun ReadCountButton(book: Book, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp),
        elevation = 5.dp,
        backgroundColor = MaterialTheme.colors.secondary
    ) {
        Box {
            Text(
                "On page ${book.currentPage} / ${book.pageCount}",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.Center)
            )
            LinearProgressIndicator(
                progress = book.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .align(Alignment.BottomCenter),
                color = MaterialTheme.colors.primaryVariant
            )
        }
    }
}

@Composable
fun Graphs(progress: List<Progress>, modifier: Modifier = Modifier) {
    if (progress.isEmpty()) return

    Spacer(Modifier.height(16.dp))

    val pagesReadData = remember(progress) {
        progress.pagesReadForLastSevenDays().map {
            LineData(it.first, it.second.toFloat())
        }
    }

    val minutesReadData = remember(progress) {
        progress.minutesReadForLastSevenDays().map {
            LineData(it.first, it.second.toFloat())
        }
    }

    if (pagesReadData.size <= 1) {
        Text(
            "Keep reading to see your progress here!",
            modifier = modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    } else {
        Text(
            text = "Pages Read",
            textAlign = TextAlign.Center,
        )
        LineChart(
            lineData = pagesReadData,
            color = MaterialTheme.colors.primaryVariant,
            modifier = modifier
                .height(250.dp)
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 32.dp),
            axisConfig = AxisConfig(
                showAxis = true,
                isAxisDashed = false,
                showUnitLabels = true,
                showXLabels = true,
                xAxisColor = MaterialTheme.colors.onSurface,
                yAxisColor = MaterialTheme.colors.onSurface,
            )
        )
    }
    Spacer(Modifier.height(32.dp))

    if (minutesReadData.size > 1) {
        Text(
            text = "Minutes Read",
            textAlign = TextAlign.Center,
        )
        LineChart(
            lineData = minutesReadData,
            color = MaterialTheme.colors.primaryVariant,
            modifier = modifier
                .height(250.dp)
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 32.dp),
            axisConfig = AxisConfig(
                showAxis = true,
                isAxisDashed = false,
                showUnitLabels = true,
                showXLabels = true,
                xAxisColor = MaterialTheme.colors.onSurface,
                yAxisColor = MaterialTheme.colors.onSurface
            ),
        )
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun AdditionalDetails(book: Book, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .padding(16.dp),
        elevation = 3.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickWithRipple {
                        expanded = !expanded
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Additional Details",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(16.dp)
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            AnimatedVisibility(visible = expanded) {
                SelectionContainer {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (book.subtitle.isNotEmpty()) Text(
                            text = book.subtitle
                        )

                        if (book.description.isNotEmpty()) Text(
                            text = book.description
                        )

                        if (book.publishingDetails.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            Text(book.publishingDetails)
                        }

                        if (book.isbn.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "ISBNs: ${book.isbn.joinToString(", ")}"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(book: Book, modifier: Modifier = Modifier) {
    Spacer(Modifier.height(16.dp))

    val thumbnail = if (book.thumbnail.isEmpty()) {
        Uri.parse("android.resource://com.namdo.bookdiary/${R.drawable.book}") // URI cho resource drawable
    } else {
        Uri.parse(book.thumbnail) // Sử dụng URI từ book.thumbnail
    }

    AsyncImage(
        model = thumbnail,
        contentDescription = "Book cover",
        modifier = modifier
            .fillMaxWidth()
            .height(256.dp)
    )
    Spacer(Modifier.height(32.dp))
    Text(
        text = book.title,
        style = MaterialTheme.typography.h5,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = book.authors.joinToString(", "),
        style = MaterialTheme.typography.subtitle1,
        textAlign = TextAlign.Center
    )
    if (book.categories.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(book.categories) {
                if (it.isNotEmpty()) Text(
                    text = it,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier
                        .background(MaterialTheme.colors.secondary)
                        .padding(8.dp),
                    color = MaterialTheme.colors.onSecondary
                )
            }
        }
    }
    if (book.ratingsCount > 0) {
        Spacer(Modifier.height(16.dp))
        Text("Rated ${book.averageRating} by ${book.ratingsCount} users")
    }
}