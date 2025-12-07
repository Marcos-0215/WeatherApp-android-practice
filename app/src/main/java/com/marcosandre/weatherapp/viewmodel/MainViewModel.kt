package com.marcosandre.weatherapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.marcosandre.weatherapp.model.City
import com.marcosandre.weatherapp.model.User

class MainViewModel : ViewModel() {   // cria classe que herda de ViewModel e gerencia dados da tela

    // NOVO (Passo 2 da Parte 2)
    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value
    // ------------------------------

    private val _cities = getCities().toMutableStateList()   // guarda lista reativa de cidades
    val cities                                              // exposição pública da lista (só leitura, para evitar modificações)
        get() = _cities.toList()
    fun remove(city: City) {                                // remove uma cidade específica
        _cities.remove(city)
    }
    fun add(name: String, location: LatLng? = null) {                                 // adiciona uma nova cidade
        _cities.add(City(name = name, location = location))
    }


}

// Gera uma lista fake de cidades, útil pra testar
private fun getCities() = List(20) { i ->
    City(name = "Cidade $i", weather = "Carregando clima...")
}