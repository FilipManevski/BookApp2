package com.example.bookapp.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookapp.data.Resource
import com.example.bookapp.model.Item
import com.example.bookapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookAppSearchViewModel @Inject constructor(private val repository: BookRepository) :
    ViewModel() {
    var listOfBooks : List<Item> by mutableStateOf(listOf())
    var isLoading: Boolean by mutableStateOf(true)

    init {
        loadBooks()
    }

    private fun loadBooks(){
        searchBooks("android")
    }

     fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()){
                return@launch
            }
            
            try {
                when(val response = repository.getBooks(query)){
                    is Resource.Success -> {
                        listOfBooks = response.data!!
                        if (listOfBooks.isNotEmpty()) isLoading = false
                    }
                    is Resource.Error -> {
                        isLoading = false
                        Log.e("Network", "loadBooks: Failed getting the books", )
                    }
                    else -> {isLoading = false}
                }
            }catch (e: java.lang.Exception){
                Log.d("Network", "loadBooks: ${e.message}")
            }
        }
    }
}