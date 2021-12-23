package com.laurentdarl.letschatapplication.domain

import com.laurentdarl.letschatapplication.data.constants.Constants.Companion.CONTENT_TYPE
import com.laurentdarl.letschatapplication.data.constants.Constants.Companion.SERVER_KEY
import com.laurentdarl.letschatapplication.data.models.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key=$SERVER_KEY", "Content-type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun pushNotification (
        @Body notification: PushNotification
        ): Response<ResponseBody>
}