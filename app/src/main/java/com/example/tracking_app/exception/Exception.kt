package com.example.tracking_app.exception

class InvalidAccessTokenException() : Exception(){
    override val message: String?
        get() = "Nom d'utilisateur ou mot de passe incorrect"
}

class NoAccessToAPIException() : Exception(){
    override val message: String?
        get() = "Désolé ! Un problème vient de survenir"
}