package com.marcosandre.weatherapp.viewmodel

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.marcosandre.weatherapp.model.City

class MainViewModel : ViewModel() {   // cria classe que herda de ViewModel e gerencia dados da tela

    private val _cities = getCities().toMutableStateList()   // guarda lista reativa de cidades
    val cities                                              // exposição pública da lista (só leitura, para evitar modificações)
        get() = _cities.toList()
    fun remove(city: City) {                                // remove uma cidade específica
        _cities.remove(city)
    }
    fun add(name: String) {                                 // adiciona uma nova cidade
        _cities.add(City(name = name))
    }


}

// Gera uma lista fake de cidades, útil pra testar
private fun getCities() = List(20) { i ->
    City(name = "Cidade $i", weather = "Carregando clima...")
}