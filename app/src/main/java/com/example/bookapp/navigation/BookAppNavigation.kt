package com.example.bookapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookapp.screens.BookAppSplashScreen
import com.example.bookapp.screens.details.BookAppDetailsScreen
import com.example.bookapp.screens.home.BookAppHomeScreen
import com.example.bookapp.screens.home.BookAppHomeScreenViewModel
import com.example.bookapp.screens.login.BookAppLoginScreen
import com.example.bookapp.screens.search.BookAppSearchViewModel
import com.example.bookapp.screens.search.SearchScreen
import com.example.bookapp.screens.stats.BookAppStatsScreen
import com.example.bookapp.screens.update.BookAppUpdateScreen

@Composable
fun BookAppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = BookAppScreens.SplashScreen.name){
        composable(BookAppScreens.SplashScreen.name){
            BookAppSplashScreen(navController = navController)
        }

        composable(BookAppScreens.LoginScreen.name){
            val homeScreenViewModel = hiltViewModel<BookAppHomeScreenViewModel>()
            BookAppLoginScreen(navController = navController)
        }

        composable(BookAppScreens.BookAppHomeScreen.name){
            val homeViewModel = hiltViewModel<BookAppHomeScreenViewModel>()
            BookAppHomeScreen(navController = navController, viewModel = homeViewModel)
        }

        composable(BookAppScreens.SearchScreen.name){
            val searchViewModel = hiltViewModel<BookAppSearchViewModel>()
            SearchScreen(navController = navController, viewModel = searchViewModel)
        }

        val detailsName = BookAppScreens.DetailScreen.name
        composable("$detailsName/{bookId}", arguments = listOf(navArgument("bookId"){
            type = NavType.StringType
        })){ backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let {
                BookAppDetailsScreen(navController = navController, bookId = it.toString())
            } }


        composable(BookAppScreens.LoginScreen.name){
            BookAppLoginScreen(navController = navController)
        }

        composable(BookAppScreens.BookAppStatsScreen.name){
            val homeScreenViewModel = hiltViewModel<BookAppHomeScreenViewModel>()
            BookAppStatsScreen(navController = navController, viewModel = homeScreenViewModel)
        }

        val updateName = BookAppScreens.UpdateScreen.name
        composable("$updateName/{bookItemId}",
             arguments = listOf(navArgument("bookItemId"){
                 type = NavType.StringType
             })
            ){navBackStackEntry ->
            navBackStackEntry.arguments?.getString("bookItemId").let {
                BookAppUpdateScreen(navController = navController, bookItemId = it.toString())
            }
        }
    }
}