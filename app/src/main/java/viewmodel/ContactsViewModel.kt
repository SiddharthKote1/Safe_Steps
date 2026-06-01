package viewmodel

import ContactDataClass.AddContactRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import repository.ContactRepository

class ContactsViewModel : ViewModel() {

    private val repository = ContactRepository()

    fun getContacts() {

        viewModelScope.launch {

            repository.getContacts()
        }
    }

    fun addContact(
        request: AddContactRequest
    ) {

        viewModelScope.launch {

            repository.addContact(request)
        }
    }

    fun updateContact(
        id: String,
        request: AddContactRequest
    ) {

        viewModelScope.launch {

            repository.updateContact(
                id,
                request
            )
        }
    }

    fun deleteContact(
        id: String
    ) {

        viewModelScope.launch {

            repository.deleteContact(id)
        }
    }
}