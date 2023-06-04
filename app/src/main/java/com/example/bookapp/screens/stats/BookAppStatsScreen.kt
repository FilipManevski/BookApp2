package com.example.bookapp.screens.stats

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.bookapp.components.BookAppBar
import com.example.bookapp.model.Item
import com.example.bookapp.model.MBook
import com.example.bookapp.navigation.BookAppScreens
import com.example.bookapp.screens.home.BookAppHomeScreenViewModel
import com.example.bookapp.utils.formatDate
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BookAppStatsScreen(navController: NavController,
                       viewModel: BookAppHomeScreenViewModel = hiltViewModel()){

    var books: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(topBar = {
        BookAppBar(title = "Book Stats", navController = navController,
        icon = Icons.Default.ArrowBack, showProfile = false){
            navController.popBackStack()
        }
    }) {
        Surface() {
            books = if (!viewModel.data.value.data.isNullOrEmpty()) {
                viewModel.data.value.data!!.filter { mBook ->
                    (mBook.userId == currentUser?.uid)
                }
            } else {
                emptyList()
            }
            Column {
                Row {
                    Box(modifier = Modifier
                        .size(45.dp)
                        .padding(2.dp)){
                        Icon(imageVector = Icons.Sharp.Person, contentDescription = "icon")
                    }
                    Text(text = "Hi ${currentUser?.email!!.split("@")[0].uppercase()}")
                }

                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                CircleShape, elevation = 5.dp) {
                    val readBooksList: List<MBook> = if (!viewModel.data.value.data.isNullOrEmpty()) {
                        books.filter { mBook ->
                            (mBook.userId == currentUser!!.uid) && (mBook.finishedReading != null)
                        }
                    } else{
                        emptyList()
                    }

                    val currentlyReadingBooks = books.filter { mBook ->
                        (mBook.startedReading != null) && mBook.finishedReading == null
                    }
                    Column(modifier = Modifier.padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                    horizontalAlignment = Alignment.Start) {
                        Text(text = "Ypur Stats", style = MaterialTheme.typography.h5)
                        Divider()
                        Text(text = "You're reading: ${currentlyReadingBooks.size} books")
                        Text(text = "You've read: ${readBooksList.size} books")
                    }
                }

                if (viewModel.data.value.loading == true){
                    LinearProgressIndicator()
                }else {
                    Divider()
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(16.dp)){
                        val readBooks : List<MBook> = if (!viewModel.data.value.data.isNullOrEmpty()){
                            viewModel.data.value.data!!.filter { mBook ->
                                (mBook.userId == currentUser?.uid) &&( mBook.finishedReading != null)
                            }
                        }else{
                            emptyList()
                        }

                        items(items = readBooks){book ->
                            BookRow2(book = book)
                        }
                    }
                }

            }
        }
    }


}

@Composable
fun BookRow2(book: MBook) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(3.dp),
        shape = RectangleShape,
        elevation = 7.dp
    ) {
        Row(modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.Top){
            val imageUrl: String = book.photoUrl!!.ifEmpty {
                "https://d28hgpri8am2if.cloudfront.net/book_images/onix/cvr9781471132254/i-am-death-9781471132254_hr.jpg"
            }
            Image(painter = rememberImagePainter(data = imageUrl)
                , contentDescription = "Book Image")
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = book.title.toString(), overflow = TextOverflow.Ellipsis)
                    if (book.rating!! >= 4) {
                        Spacer(modifier = Modifier.fillMaxWidth(0.8f))
                        Icon(imageVector = Icons.Default.ThumbUp, contentDescription = "thumb's up",
                        tint = Color.Green.copy(alpha = 0.5f)
                        )
                    } else {
                        Box{}
                    }
                }
                Text(text = "Author: ${book.authors}", softWrap = true, overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)

                Text(text = "Started reading: ${formatDate(book.startedReading!!)}", softWrap = true, overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)

                Text(text = "Finished reading: ${formatDate(book.finishedReading!!)}", softWrap = true, overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)
                
            }
        }
    }
}