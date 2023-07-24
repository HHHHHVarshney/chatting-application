package com.example.firebasepractice.models

class MessageModel {
    var uId: String = ""
    var message: String = ""
    var time: Long = 0
    var messageId:String = ""

    constructor(uId: String, message: String, time: Long) {
        this.uId = uId
        this.message = message
        this.time = time
    }
    constructor()
    constructor(uId: String, message: String) {
        this.uId = uId
        this.message = message
    }
}