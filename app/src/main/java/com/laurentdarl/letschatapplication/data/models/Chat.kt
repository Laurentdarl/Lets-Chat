package com.laurentdarl.letschatapplication.data.models

import java.io.Serializable

data class Chat(
    val senderId: String? = null,
    val receiverId: String? = null,
    val message: String? = null
): Serializable {}