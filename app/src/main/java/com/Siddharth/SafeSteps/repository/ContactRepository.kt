package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.contactdataclass.AddContactRequest
import com.Siddharth.SafeSteps.data.ApiService

class ContactRepository(private val api: ApiService) {

    suspend fun getContacts() =
        api.getContacts()

    suspend fun addContact(request: AddContactRequest) =
        api.addContact(request)

    suspend fun updateContact(
        id: String,
        request: AddContactRequest
    ) = api.updateContact(id, request)

    suspend fun deleteContact(id: String) =
        api.deleteContact(id)
}