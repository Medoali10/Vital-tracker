package com.example.pdapplication.hospitago

class card{

    var username :String?=null
    var phone :String?= null
    var mood :String? = null
    var position :String? = null
    var adress :String? = null
    var phone2 :String? = null



    constructor(username: String?,phone: String?,
                mood: String?, position: String?,
                adress: String?, phone2: String?


    ) {
        this.mood= mood
        this.adress= adress
        this.phone2= phone2
        this.username = username
        this.phone = phone
        this.position= position

    }


}