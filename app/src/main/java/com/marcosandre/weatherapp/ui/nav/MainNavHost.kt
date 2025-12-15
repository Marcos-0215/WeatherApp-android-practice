package com.marcosandre.weatherapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.marcosandre.weatherapp.ui.HomePage
import com.marcosandre.weatherapp.ui.ListPage
import com.marcosandre.weatherapp.viewmodel.MainViewModel
import com.marcosandre.weatherapp.ui.MapPage

@Composable
fun MainNavHost(navController: NavHostController,
                viewModel: MainViewModel
) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(viewModel = viewModel) }
        composable<Route.List> { ListPage(viewModel = viewModel) }
        composable<Route.Map> { MapPage(viewModel = viewModel) }
    }
}