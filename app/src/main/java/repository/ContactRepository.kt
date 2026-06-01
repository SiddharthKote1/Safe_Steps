package repository

import ContactDataClass.AddContactRequest
import data.RetrofitClient

class ContactRepository {

    private val api = RetrofitClient.apiService

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