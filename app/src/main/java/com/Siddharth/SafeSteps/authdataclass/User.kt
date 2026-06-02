package com.Siddharth.SafeSteps.authdataclass

data class RegisterResponse(
    val success: Boolean,
    val token: String,
    val user: UserX
)