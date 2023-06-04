package com.example.bookapp.screens.details

import androidx.lifecycle.ViewModel
import com.example.bookapp.data.Resource
import com.example.bookapp.model.Item
import com.example.bookapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BookAppDetailsViewModel @Inject constructor(private val repository: BookRepository) : ViewModel(){

    suspend fun getBookInfo(bookId: String) : Resource<Item> {
        return repository.getBookInfo(bookId)
    }

}