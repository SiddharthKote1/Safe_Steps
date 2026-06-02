package com.Siddharth.SafeSteps.profilesdataclass

data class SimUpdateRequest(
    val carrier: String,
    val name: String,
    val number: String,
    val simId: Int
)