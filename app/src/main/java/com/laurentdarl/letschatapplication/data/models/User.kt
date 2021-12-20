package com.laurentdarl.letschatapplication.data.models

data class User(
    val userName: String? = null,
    val email: String? = null,
    val userId: String? = null,
    val image: String? = null
) {}

//class User {
//    var userName: String? = null
//    var email: String? = null
//    var userId: String? = null
//
//    constructor() {}
//
//    constructor(userName: String?, email: String?, userId: String?) {
//        this.userName = userName
//        this.email = email
//        this.userId = userId
//    }
//}
