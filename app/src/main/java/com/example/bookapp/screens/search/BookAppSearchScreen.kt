package com.example.bookapp.screens.search

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.bookapp.components.BookAppBar
import com.example.bookapp.components.InputField
import com.example.bookapp.model.Item
import com.example.bookapp.navigation.BookAppScreens

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(navController: NavController, viewModel: BookAppSearchViewModel = hiltViewModel()){

    Scaffold(topBar = {
        BookAppBar(title = "Search Books",
            navController = navController,
            icon = Icons.Default.ArrowBack,
            showProfile = false){
            navController.navigate(BookAppScreens.BookAppHomeScreen.name)
        }
    }) {
        Surface {
            Column {
                SearchForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    viewModel
                    ) {searchQuery ->
                    viewModel.searchBooks(searchQuery)
                }
                Spacer(modifier = Modifier.height(13.dp))
                BookList(navController, viewModel)
            }
        }
    }

}

@Composable
fun BookList(navController: NavController, viewModel: BookAppSearchViewModel = hiltViewModel()) {

    val listOfBooks = viewModel.listOfBooks
    if (viewModel.isLoading){
        LinearProgressIndicator()
    }else{
        LazyColumn(modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)){
            items(items = listOfBooks){book ->
                BookRow(book, navController)
            }
        }
    }
}

@Composable
fun BookRow(book: Item, navController: NavController) {
    Card(modifier = Modifier
        .clickable {
            navController.navigate(BookAppScreens.DetailScreen.name + "/${book.id}")
        }
        .fillMaxWidth()
        .height(100.dp)
        .padding(3.dp),
    shape = RectangleShape,
        elevation = 7.dp
        ) {
        Row(modifier = Modifier.padding(5.dp),
        verticalAlignment = Alignment.Top){
            val imageUrl: String = book.volumeInfo.imageLinks.smallThumbnail.ifEmpty {
                "https://d28hgpri8am2if.cloudfront.net/book_images/onix/cvr9781471132254/i-am-death-9781471132254_hr.jpg"
            }
            Image(painter = rememberImagePainter(data = imageUrl)
                , contentDescription = "Book Image")
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = book.volumeInfo.title, overflow = TextOverflow.Ellipsis)
                Text(text = "Author: ${book.volumeInfo.authors}", overflow = TextOverflow.Clip,
                style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)

                Text(text = "Date: ${book.volumeInfo.authors}", overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)

                Text(text = "Publisher: ${book.volumeInfo.publisher}", overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
    viewModel: BookAppSearchViewModel,
    loading: Boolean = false,
    hint: String = "Search",
    onSearch: (String) -> Unit = {}
){
    Column {
        val searchQuery = rememberSaveable { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQuery.value) { searchQuery.value.trim().isNotEmpty() }

        InputField(valueState = searchQuery, labelId = "Search", enabled = true,
                   onAction = KeyboardActions{
                       if (!valid) return@KeyboardActions
                       onSearch(searchQuery.value.trim())
                       searchQuery.value = ""
                       keyboardController?.hide()
                   }
            )

    }
}