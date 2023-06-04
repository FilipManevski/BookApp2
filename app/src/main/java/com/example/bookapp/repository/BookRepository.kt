package com.example.bookapp.repository

import com.example.bookapp.data.DataOrException
import com.example.bookapp.data.Resource
import com.example.bookapp.model.Book
import com.example.bookapp.model.Item
import com.example.bookapp.network.BooksApi
import javax.inject.Inject

class BookRepository @Inject constructor(private val api: BooksApi) {

    suspend fun getBooks(searchQuery: String): Resource<List<Item>> {
       return try {
            Resource.Loading(data = "Loading...")
            val itemList = api.getAllBooks(searchQuery).items
           if (itemList.isNotEmpty()) Resource.Loading(false)
            Resource.Success(data = itemList)

        }catch (e: Exception){
            Resource.Error(message = e.message.toString())
        }
    }

    suspend fun getBookInfo(bookId: String) : Resource<Item> {
        val response = try {
            Resource.Loading(true)
            api.bookInfo(bookId)
        } catch (e: Exception){
            return Resource.Error(message = e.message.toString())
        }
        Resource.Loading(false)
        return Resource.Success(response)
    }
}