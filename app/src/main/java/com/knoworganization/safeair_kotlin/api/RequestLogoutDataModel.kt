package com.knoworganization.safeair_kotlin.api

data class RequestLogoutDataModel(
    val date: String,
    val logInTime: String,
    val logOutTime: String,
    val email: String,
    val sessionNumber: Int,
    val logInLat: String,
    val logInLng: String,
    val logOutLat: String,
    val logOutLng: String
)
