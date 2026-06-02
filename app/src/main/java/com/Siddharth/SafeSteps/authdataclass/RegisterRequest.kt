package com.Siddharth.SafeSteps.authdataclass

data class RegisterRequest(
    val age: String,
    val blood_group: String,
    val date_of_birth: String,
    val full_name: String,
    val gender: String,
    val medical_notes: String,
    val phone: String,
    val preferred_language: String,
    val password: String? = null
)