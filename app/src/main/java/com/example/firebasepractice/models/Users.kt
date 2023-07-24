package com.example.firebasepractice.models

class Users {
    var username: String? = ""
    var eMail: String = ""
    var profilePic: String = ""
    var uId: String = ""
    var password: String = ""
    var status:String? = ""


    constructor()


    constructor(username: String, eMail: String, password: String) {
        this.username = username
        this.eMail = eMail
        this.password = password
    }
}





