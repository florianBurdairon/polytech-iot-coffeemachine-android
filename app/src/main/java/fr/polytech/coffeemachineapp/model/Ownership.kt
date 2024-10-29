package fr.polytech.coffeemachineapp.model

data class Owner(val uid: String = "", val ownership: List<Ownership> = listOf())

data class Ownership(val guests: List<String>? = null, val mac: String = "", val type: String = "")