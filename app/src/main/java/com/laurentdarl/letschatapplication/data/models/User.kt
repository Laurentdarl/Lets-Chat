package com.laurentdarl.letschatapplication.data.models

import java.io.Serializable

data class User(
    val userName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val userId: String? = null,
    val profileImage: String? = null
): Serializable {}

