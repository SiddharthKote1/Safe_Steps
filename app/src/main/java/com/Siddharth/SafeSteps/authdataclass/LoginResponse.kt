package com.Siddharth.SafeSteps.authdataclass

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val user: UserX
)