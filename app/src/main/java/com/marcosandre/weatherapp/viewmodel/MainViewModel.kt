package com.marcosandre.weatherapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.marcosandre.weatherapp.db.fb.FBCity
import com.marcosandre.weatherapp.db.fb.FBDatabase
import com.marcosandre.weatherapp.db.fb.FBUser
import com.marcosandre.weatherapp.db.fb.toFBCity
import com.marcosandre.weatherapp.model.City
import com.marcosandre.weatherapp.model.User

class MainViewModel(
    private val db: FBDatabase
) : ViewModel(), FBDatabase.Listener {


    private val _cities = mutableStateListOf<City>()
    val cities get() = _cities.toList()

    // NOVO (Passo 2 da Parte 2)
    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value
    // ------------------------------

    init {
        // Muito importante: ViewModel agora escuta o Firebase
        db.setListener(this)
    }

    // Chamado pela UI (ex: ao clicar em Add City)
    fun add(name: String, location: LatLng? = null) {
        db.add(
            City(name = name, location = location).toFBCity()
        )
    }

    fun remove(city: City) {
        db.remove(
            city.toFBCity()
        )
    }

    // Implementação dos callbacks do Firebase

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        //TODO("Not yet implemented")
    }

    override fun onCityAdded(city: FBCity) {
        _cities.add(city.toCity())
    }

    override fun onCityUpdated(city: FBCity) {
        //TODO("Not yet implemented")
    }

    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.toCity())
    }


}

class MainViewModelFactory(
    private val db: FBDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

