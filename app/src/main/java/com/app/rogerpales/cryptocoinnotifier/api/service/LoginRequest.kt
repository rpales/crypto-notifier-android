package com.app.rogerpales.cryptocoinnotifier.api.service

data class LoginRequest(
        val email: String,
        val password: String,
        val device_id: String
)