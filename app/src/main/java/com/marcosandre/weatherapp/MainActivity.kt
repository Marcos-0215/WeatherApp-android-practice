package com.marcosandre.weatherapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.util.Consumer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.marcosandre.weatherapp.ui.theme.WeatherAppTheme
import com.marcosandre.weatherapp.viewmodel.MainViewModel
import com.marcosandre.weatherapp.ui.CityDialog
import com.marcosandre.weatherapp.ui.nav.BottomNavBar
import com.marcosandre.weatherapp.ui.nav.BottomNavItem
import com.marcosandre.weatherapp.ui.nav.MainNavHost
import com.marcosandre.weatherapp.ui.nav.Route
import androidx.navigation.NavDestination.Companion.hasRoute
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.marcosandre.weatherapp.api.WeatherService
import com.marcosandre.weatherapp.db.fb.FBDatabase
import com.marcosandre.weatherapp.db.local.LocalDatabase
import com.marcosandre.weatherapp.monitor.ForecastMonitor
import com.marcosandre.weatherapp.repo.Repository
import com.marcosandre.weatherapp.viewmodel.MainViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Pratica 6, parte 3, passo 3

            // Instância única do FBDatabase e WeatherService durante toda a composição
            val fbDB = remember { FBDatabase() }
            val weatherService = remember { WeatherService(this) }

            // Pratica 10
            val forecastMonitor = remember { ForecastMonitor(this) }

            // Pratica 11
            val uid = Firebase.auth.currentUser?.uid ?: "anonymous"

            val localDB = remember(uid) {
                LocalDatabase(
                    context = this,
                    databaseName = "weatherapp_$uid.db"
                )
            }

            val repository = remember {
                Repository(
                    fbDB = fbDB,
                    localDB = localDB
                )
            }



            /*
            // ViewModel usando a factory
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(fbDB, weatherService)
            )
            */

            // Pratica 10
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    // fbDB,
                    repository, // Pratica 11
                    weatherService,
                    forecastMonitor
                )
            )

            DisposableEffect(Unit) {

                val listener = Consumer<Intent> { intent ->
                    viewModel.city = intent.getStringExtra("city")
                    viewModel.page = Route.Home
                }

                addOnNewIntentListener(listener)

                onDispose {
                    removeOnNewIntentListener(listener)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        1001
                    )
                }
            }



            ////////////////
            //val viewModel : MainViewModel by viewModels() // Versão ANTERIOR, até prática 6, parte 3.
            val navController = rememberNavController()
            var showDialog by remember { mutableStateOf(false) }

            val currentRoute = navController.currentBackStackEntryAsState()
            val showButton = currentRoute.value?.destination?.hasRoute(Route.List::class) == true
            val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {} )

            WeatherAppTheme {


                // Faz o Compose renderizar o CityDialog qnd showDialog for true
                if (showDialog) {
                    CityDialog(
                        onDismiss = { showDialog = false },
                        onConfirm = { city ->
                            if (city.isNotBlank()) viewModel.addCity(city)
                            showDialog = false
                        }
                    )
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                val name = viewModel.user?.name?:"[carregando...]"
                                Text("Bem-vindo(a)! $name")
                                    },

                            actions = {

                                IconButton(
                                    onClick = {
                                        Firebase.auth.signOut()
                                    }
                                ) {
                                    Icon(
                                        imageVector =
                                            Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        )
                    },

                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.HomeButton,
                            BottomNavItem.ListButton,
                            BottomNavItem.MapButton,

                            )

                        // MUDANÇA na prática 08
                        //BottomNavBar(navController = navController, items)
                        BottomNavBar(viewModel, items)

                    },

                    floatingActionButton = {

                        if (showButton) {
                            FloatingActionButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Adicionar")
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        MainNavHost(navController = navController,
                            viewModel = viewModel)
                    }

                    LaunchedEffect(viewModel.page) {
                        navController.navigate(viewModel.page) {
                            // Volta pilha de navegação até HomePage (startDest)
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                            launchSingleTop = true
                        }
                    }


                }
            }
        }
    }
}



