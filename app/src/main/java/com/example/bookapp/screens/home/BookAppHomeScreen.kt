package com.example.bookapp.screens.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bookapp.components.*
import com.example.bookapp.model.MBook
import com.example.bookapp.navigation.BookAppScreens
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BookAppHomeScreen(
    navController: NavController, viewModel: BookAppHomeScreenViewModel = hiltViewModel()
){
    Scaffold(topBar = {
                      BookAppBar(title = "Reader", navController = navController)
    },
    floatingActionButton = {
        FABContent{
            navController.navigate(BookAppScreens.SearchScreen.name)
        }
    }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            HomeContent(navController, viewModel)
        }
    }
}



@Composable
fun HomeContent(navController: NavController, viewModel: BookAppHomeScreenViewModel){

    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (!viewModel.data.value.data.isNullOrEmpty()){
        listOfBooks = viewModel.data.value.data!!.toList().filter {mBook ->
            mBook.userId == currentUser?.uid.toString()
        }
        Log.d("Books", "HomeContent: $listOfBooks")
    }

//    val listOfBooks = listOf(
//        MBook(id = "carter1", "THE CRUCIFIX KILLER", "Chris Carter", "If he's watching you you are dead!"),
//        MBook(id = "carter2", "THE EXECUTIONER", "Chris Carter", "If he's watching you you are dead!"),
//        MBook(id = "carter3", "THE NIGHT STALKER", "Chris Carter", "If he's watching you you are dead!"),
//        MBook(id = "carter4", "ONE BY ONE", "Chris Carter", "If he's watching you you are dead!"),
//        MBook(id = "carter5", "I AM DEATH", "Chris Carter", "If he's watching you you are dead!"),
//    )

    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if (!email.isNullOrEmpty())
        email.split("@")[0] else "N/A"


    Column(modifier = Modifier.padding(2.dp),
    verticalArrangement = Arrangement.Top) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            TitleSection(label = "Currently reading.")
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            Column {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(BookAppScreens.BookAppStatsScreen.name)
                        }
                        .size(45.dp),
                    tint = MaterialTheme.colors.secondaryVariant
                )
                Text(text = currentUserName,
                modifier = Modifier.padding(2.dp),
                style = MaterialTheme.typography.overline,
                color = Color.Red,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip)
                Divider()
            }
        }
        ReadingRightNowArea(listOfBooks = listOfBooks, navController = navController)
        TitleSection(label = "Reading list")

        BookListArea(listOfBooks = listOfBooks, navController = navController)
    }
}

@Composable
fun BookListArea(listOfBooks: List<MBook>, navController: NavController) {
    val addedBooks = listOfBooks.filter { mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }
    HorizontalScrollableComponent(addedBooks){
       navController.navigate(BookAppScreens.UpdateScreen.name + "/$it")
    }
}

@Composable
fun HorizontalScrollableComponent(listOfBooks: List<MBook>,
                                  viewModel: BookAppHomeScreenViewModel = hiltViewModel(),
                                  onCardPressed: (String) -> Unit) {
    val scrollState = rememberScrollState()

    Row(modifier = Modifier
        .fillMaxWidth()
        .heightIn(280.dp)
        .horizontalScroll(scrollState)) {
        if (viewModel.data.value.loading == true){
            LinearProgressIndicator()
        }else{
            if (listOfBooks.isEmpty()){
                Surface(modifier = Modifier.padding(23.dp)) {
                    Text(text = "no books found. Add a book.",
                    style = TextStyle(color = Color.Red.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp))
                }
            }else{
                for (book in listOfBooks){
                    BookCard(book){
                        onCardPressed(book.googleBookId.toString())
                    }
            }
        }

        }
    }
}


@Composable
fun ReadingRightNowArea(listOfBooks: List<MBook>, navController: NavController){

    val readingNowList = listOfBooks.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }

   HorizontalScrollableComponent(listOfBooks = readingNowList){
       navController.navigate(BookAppScreens.UpdateScreen.name + "/$it")
   }
}




