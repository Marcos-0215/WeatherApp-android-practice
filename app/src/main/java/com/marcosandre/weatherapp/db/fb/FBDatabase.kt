package com.marcosandre.weatherapp.db.fb

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class FBDatabase {

    // Listener que será implementado pelo ViewModel
    interface Listener {
        fun onUserLoaded(user: FBUser)
        fun onUserSignOut()
        fun onCityAdded(city: FBCity)
        fun onCityUpdated(city: FBCity)
        fun onCityRemoved(city: FBCity)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var citiesListReg: ListenerRegistration? = null

    // Quem receberá os eventos
    private var listener: Listener? = null

    init {
        auth.addAuthStateListener { auth ->

            // Caso o usuário NÃO esteja logado → limpa listeners e avisa o ViewModel
            if (auth.currentUser == null) {
                citiesListReg?.remove()
                listener?.onUserSignOut()
                return@addAuthStateListener
            }

            // Referência ao documento do usuário atual
            val refCurrUser = db.collection("users")
                .document(auth.currentUser!!.uid)

            // Carrega os dados do usuário e dispara evento
            refCurrUser.get()
                .addOnSuccessListener {
                    it.toObject(FBUser::class.java)?.let { user ->
                        listener?.onUserLoaded(user)
                    }
                }

            // Escuta alterações em "users/{uid}/cities"
            citiesListReg = refCurrUser
                .collection("cities")
                .addSnapshotListener { snapshots, ex ->

                    if (ex != null) return@addSnapshotListener

                    snapshots?.documentChanges?.forEach { change ->

                        val fbCity = change.document.toObject(FBCity::class.java)

                        when (change.type) {
                            DocumentChange.Type.ADDED ->
                                listener?.onCityAdded(fbCity)

                            DocumentChange.Type.MODIFIED ->
                                listener?.onCityUpdated(fbCity)

                            DocumentChange.Type.REMOVED ->
                                listener?.onCityRemoved(fbCity)
                        }
                    }
                }
        }
    }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
    }

    fun register(user: FBUser) {
        // registra o usuário no BD (criar documento)
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")

        val uid = auth.currentUser!!.uid

        db.collection("users")
            .document(uid + "")
            .set(user)
    }

    fun add(city: FBCity) {
        // adiciona cidade no BD
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")

        if (city.name == null || city.name!!.isEmpty())
            throw RuntimeException("City with null or empty name!")

        val uid = auth.currentUser!!.uid

        db.collection("users")
            .document(uid)
            .collection("cities")
            .document(city.name!!)
            .set(city)
    }

    fun remove(city: FBCity) {
        // remove cidade do BD
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")

        if (city.name == null || city.name!!.isEmpty())
            throw RuntimeException("City with null or empty name!")

        val uid = auth.currentUser!!.uid

        db.collection("users")
            .document(uid)
            .collection("cities")
            .document(city.name!!)
            .delete()
    }

    // Adicionado na pratica 10
    fun update(city: FBCity) {
        if (auth.currentUser == null)
            throw RuntimeException("Not logged in!")

        val uid = auth.currentUser!!.uid

        val changes = mapOf(
            "lat" to city.lat,
            "lng" to city.lng,
            "monitored" to city.monitored
        )

        db.collection("users")
            .document(uid)
            .collection("cities")
            .document(city.name!!)
            .update(changes)
    }

}
