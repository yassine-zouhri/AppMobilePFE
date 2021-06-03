package com.example.tracking_app.models

data class EventFBRLresponse (
    val id : Long = 1 ,
    val titre : String = "titre",
    val description : String = "description",
    val degre_danger : Int = 1,
    val longitude : Double = 32.0,
    val latitude : Double = -5.0,
    val imageURL : String = "imageURL",
    val dejavue : Boolean = false,
    val date : Long = 0 ,
    var zone : String ="",
    val categorie : String = "",
    val token : String = ""
)