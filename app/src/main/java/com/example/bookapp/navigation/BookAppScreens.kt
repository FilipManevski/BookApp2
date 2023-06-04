package com.example.bookapp.navigation

import okhttp3.Route

enum class BookAppScreens {
    SplashScreen,
    LoginScreen,
    CreateAccountScreen,
    BookAppHomeScreen,
    SearchScreen,
    DetailScreen,
    UpdateScreen,
    BookAppStatsScreen;


    companion object {
        fun fromRoute(route: String): BookAppScreens
        = when(route?.substringBefore("/")){
            SplashScreen.name -> SplashScreen
            LoginScreen.name -> LoginScreen
            CreateAccountScreen.name -> CreateAccountScreen
            BookAppHomeScreen.name -> BookAppHomeScreen
            SearchScreen.name -> SearchScreen
            DetailScreen.name -> DetailScreen
            UpdateScreen.name -> UpdateScreen
            BookAppStatsScreen.name -> BookAppStatsScreen
            null -> BookAppHomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}