package com.marcosandre.weatherapp.db.fb

import com.google.android.gms.maps.model.LatLng
import com.marcosandre.weatherapp.model.City

class FBCity() {

    // Firestore exige propriedades públicas e mutáveis
    var name: String? = null
    var lat: Double? = null
    var lng: Double? = null

    // Converte FBCity -> City (do app)
    fun toCity(): City {
        val latlng =
            if (lat != null && lng != null)
                LatLng(lat!!, lng!!)
            else null

        return City(
            name = name!!,
            //weather = null,
            location = latlng
        )
    }
}

// Converte City -> FBCity (para mandar pro Firestore)
fun City.toFBCity(): FBCity {
    val fbCity = FBCity()
    fbCity.name = this.name
    fbCity.lat = this.location?.latitude ?: 0.0
    fbCity.lng = this.location?.longitude ?: 0.0
    return fbCity
}