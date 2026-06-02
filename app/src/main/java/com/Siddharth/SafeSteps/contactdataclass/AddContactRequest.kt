package com.Siddharth.SafeSteps.contactdataclass

data class AddContactRequest(
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val priority: String
)
