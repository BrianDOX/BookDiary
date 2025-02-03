package com.namdo.bookdiary.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.namdo.bookdiary.data.Book
import com.namdo.bookdiary.data.Progress
import com.namdo.bookdiary.util.Converters

@Database(entities = [Book::class, Progress::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
public abstract class BookDiaryDatabase : RoomDatabase() {
    abstract fun bookDao(): BooksDao
    abstract fun progressDao(): ProgressDao
}