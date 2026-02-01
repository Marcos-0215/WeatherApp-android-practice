package com.marcosandre.weatherapp.viewmodel

import androidx.browser.browseractions.BrowserServiceFileProvider.loadBitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.marcosandre.weatherapp.api.WeatherService
import com.marcosandre.weatherapp.api.toForecast
import com.marcosandre.weatherapp.api.toWeather
import com.marcosandre.weatherapp.db.fb.FBCity
import com.marcosandre.weatherapp.db.fb.FBDatabase
import com.marcosandre.weatherapp.db.fb.FBUser
import com.marcosandre.weatherapp.db.fb.toFBCity
import com.marcosandre.weatherapp.model.City
import com.marcosandre.weatherapp.model.Forecast
import com.marcosandre.weatherapp.model.User
import com.marcosandre.weatherapp.model.Weather
import com.marcosandre.weatherapp.ui.nav.Route

class MainViewModel(
    private val db: FBDatabase,
    private val service: WeatherService
) : ViewModel(), FBDatabase.Listener {

    // ATUALIZADO na pratica 08
    //private val _cities = mutableStateListOf<City>()
    private val _cities = mutableStateMapOf<String, City>()

    // ATUALIZADO na pratica 08
    //val cities get() = _cities.toList()
    val cities: List<City>
        get() = _cities.values
            .toList()
            .sortedBy { it.name }

    private val _weather = mutableStateMapOf<String, Weather>()
    private val _forecast = mutableStateMapOf<String, List<Forecast>?>()

    // NOVO (Passo 2 da Parte 2)
    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value
    // ------------------------------

    private val _city = mutableStateOf<String?>(null)

    var city: String?
        get() = _city.value
        set(tmp) {
            _city.value = tmp
        }

    private var _page = mutableStateOf<Route>(Route.Home)

    var page: Route
        get() = _page.value
        set(tmp) { _page.value = tmp }


    val cityMap: Map<String, City>
        get() = _cities.toMap()



    init {
        // Muito importante: ViewModel agora escuta o Firebase
        db.setListener(this)
    }

    // Chamado pela UI (ex: ao clicar em Add City)
    // OBSOLETO PELOS NOVOS MÉTODOS USANDO API
    /*
    fun add(name: String, location: LatLng? = null) {
        db.add(
            City(name = name, location = location).toFBCity()
        )
    }
    */

    fun addCity(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                db.add(
                    City(
                        name = name,
                        location = LatLng(lat, lng)
                    ).toFBCity()
                )
            }
        }
    }

    fun addCity(location: LatLng) {
        service.getName(location.latitude, location.longitude) { name ->
            if (name != null) {
                db.add(
                    City(
                        name = name,
                        location = location
                    ).toFBCity()
                )
            }
        }
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

    /*
    override fun onCityAdded(city: FBCity) {
        _cities.add(city.toCity())
    }
    */
    override fun onCityAdded(city: FBCity) {
        _cities[city.name!!] = city.toCity()
    }


    override fun onCityUpdated(city: FBCity) {

        _cities.remove(city.name)
        _cities[city.name!!] = city.toCity()
    }

    /*
    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.toCity())
    }
    */
    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.name)
    }


    fun weather(name: String) =
        _weather.getOrPut(name) {
            loadWeather(name)
            Weather.LOADING
        }


    private fun loadWeather(name: String) {
        service.getWeather(name) { apiWeather ->
            apiWeather?.let {
                _weather[name] = apiWeather.toWeather()  // Converte a resposta para o objeto Weather e armazena no map
                loadBitmap(name) // Pratica 09
            }
        }
    }

    fun forecast(name: String) =
        _forecast.getOrPut(name) {
            loadForecast(name)
            emptyList()   // valor retornado imediatamente
        }

    private fun loadForecast(name: String) {
        service.getForecast(name) { apiForecast ->
            apiForecast?.let {
                _forecast[name] = apiForecast.toForecast()
            }
        }
    }

    fun loadBitmap(name: String) {
        _weather[name]?.let { weather ->
            service.getBitmap(weather.imgUrl) { bitmap ->
                _weather[name] = weather.copy(bitmap = bitmap)
            }
        }
    }

    fun update(city: City) {
        db.update(city.toFBCity())
    }


}

class MainViewModelFactory(
    private val db: FBDatabase,
    private val service: WeatherService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db, service) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

