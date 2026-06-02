package com.Siddharth.SafeSteps.authdataclass

data class LoginRequest(
    val phone: String,
    val password: String? = null
)