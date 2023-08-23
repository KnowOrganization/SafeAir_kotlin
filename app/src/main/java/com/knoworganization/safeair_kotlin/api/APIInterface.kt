package com.knoworganization.safeair_kotlin.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIInterface {
    @POST("/addLoginData")
    fun requestSendLoginData(@Body requestModel: RequestLogInDataModel) :Call<ResponseClass>

    @POST("/addLogoutData")
    fun requestSendLogoutData(@Body requestModel: RequestLogoutDataModel) :Call<ResponseClass>
}