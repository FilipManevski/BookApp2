package com.example.bookapp.screens.details

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.bookapp.components.BookAppBar
import com.example.bookapp.components.FABContent
import com.example.bookapp.components.RoundedButton
import com.example.bookapp.data.Resource
import com.example.bookapp.model.Item
import com.example.bookapp.model.MBook
import com.example.bookapp.navigation.BookAppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "ProduceStateDoesNotAssignValue")
@Composable
fun BookAppDetailsScreen(navController: NavController, bookId: String,
                         viewModel: BookAppDetailsViewModel = hiltViewModel()){

    Scaffold(topBar = {
        BookAppBar(title = "Book Details",
            icon = Icons.Default.ArrowBack,
            navController = navController,
            showProfile = false
            ) {
            navController.navigate(BookAppScreens.SearchScreen.name)
        }
    }) {

        Surface(modifier = Modifier
            .padding(3.dp)
            .fillMaxSize()) {
            Column(modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {

                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()){
                    value = viewModel.getBookInfo(bookId)
                }.value
                if (bookInfo.data == null){
                    Row {
                       LinearProgressIndicator()
                       Text(text = "Loading..")
                    }
                } else {
                    ShowBookDetails(bookInfo, navController)
                }
            }
        }
    }
    
    
}

@Composable
fun ShowBookDetails(bookInfo: Resource<Item>,
                    navController: NavController) {
    val bookData = bookInfo.data?.volumeInfo
    val googleBookId = bookInfo.data?.id

    Card(modifier = Modifier.padding(34.dp),
    shape = CircleShape, elevation = 4.dp) {
        Image(painter = rememberImagePainter(data = bookData!!.imageLinks.thumbnail),
            contentDescription = "Book Image",
            modifier = Modifier
                .width(90.dp)
                .height(90.dp)
                .padding(1.dp))
    }
    Text(text = bookData!!.title
    , style = MaterialTheme.typography.h6
    , overflow = TextOverflow.Ellipsis
    , maxLines = 19)
    Text(text = "Authors: ${bookData.authors}")
    Text(text = "Page Count: ${bookData.pageCount}")
    Text(text = "Categories: ${bookData.categories}"
    , style = MaterialTheme.typography.subtitle1,
        overflow = TextOverflow.Ellipsis, maxLines = 3)
    Text(text = "Published: ${bookData.publishedDate}"
        , style = MaterialTheme.typography.subtitle1)

    Spacer(modifier = Modifier.height(5.dp))
    
    val cleanDescription = HtmlCompat.fromHtml(bookData.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
    val localDims = LocalContext.current.resources.displayMetrics
    Surface(modifier = Modifier
        .height(localDims.heightPixels.dp.times(0.09f))
        .padding(4.dp), shape = RectangleShape, border = BorderStroke(1.dp, color = Color.DarkGray)
    ) {
        LazyColumn(modifier = Modifier.padding(3.dp)){
            item {
                Text(text = "$cleanDescription")
            }
        }
    }

    Row(horizontalArrangement = Arrangement.SpaceEvenly
    , modifier = Modifier.padding(top = 6.dp)) {
        RoundedButton("Save"){
            //save this book to the firestore database
            val book = MBook(
                title = bookData.title,
                authors = bookData.authors.toString(),
                description = bookData.description,
                categories = bookData.categories.toString(),
                notes = "",
                photoUrl = bookData.imageLinks.thumbnail,
                publishedDate = bookData.publishedDate,
                pageCount = bookData.pageCount.toString(),
                rating = 0.0,
                googleBookId = googleBookId,
                userId = FirebaseAuth.getInstance().currentUser?.uid
            )
            saveToFirebase(book, navController)
        }
        Spacer(modifier = Modifier.width(25.dp))
        RoundedButton("Cancel"){
            navController.popBackStack()
        }
    }
}

fun saveToFirebase(book: MBook, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")

    if (book.toString().isNotEmpty()){
        dbCollection.add(book)
            .addOnSuccessListener { documentRef ->
                val docId = documentRef.id
                dbCollection.document(docId)
                    .update(hashMapOf("id" to docId) as Map<String, Any>)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful){
                            navController.popBackStack()
                        }
                    }.addOnFailureListener {
                        Log.w("Error", "saveToFirebase: Error updating doc", it)
                    }
            }
    }else{
        Log.d("STF", "saveToFirebase: Error")
    }
}
