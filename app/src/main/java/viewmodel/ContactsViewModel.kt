package viewmodel

import ContactDataClass.AddContactRequest
import ContactDataClass.Contact
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import repository.ContactRepository

class ContactsViewModel : ViewModel() {

    private val repository = ContactRepository()

    var contacts by mutableStateOf<List<Contact>>(emptyList())
        private set

    fun getContacts() {

        viewModelScope.launch {

            contacts =
                repository.getContacts()
        }
    }

    fun addContact(
        request: AddContactRequest
    ) {

        viewModelScope.launch {

            repository.addContact(request)

            getContacts()
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

            getContacts()
        }
    }

    fun deleteContact(
        id: String
    ) {

        viewModelScope.launch {

            repository.deleteContact(id)

            getContacts()
        }
    }
}