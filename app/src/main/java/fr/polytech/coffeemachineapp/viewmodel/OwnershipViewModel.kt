package fr.polytech.coffeemachineapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import fr.polytech.coffeemachineapp.model.Owner
import fr.polytech.coffeemachineapp.model.Ownership
import fr.polytech.coffeemachineapp.model.QRData
import fr.polytech.coffeemachineapp.repository.FirebaseRepository
import fr.polytech.coffeemachineapp.utils.Constant.Companion.OWNERSHIPS_PATH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OwnershipViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    private var _selectedOwner = MutableStateFlow<Owner?>(null)
    val selectedOwnerState: StateFlow<Owner?> = _selectedOwner
    private var _guests = MutableStateFlow<List<String>>(emptyList())
    val guestsState: StateFlow<List<String>> = _guests

    private val ownerListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val userOwnership = mutableListOf<Ownership>()
            for (ownership in snapshot.children) {
                try {
                    val data = ownership.getValue(Ownership::class.java)
                    if (data != null) {
                        userOwnership.add(data)
                    }
                }
                catch (e: Exception) {
                    Log.e("Firebase", "Error: ${e.message}")
                }
            }
            _selectedOwner.update { Owner(snapshot.key!!, userOwnership) }
            Log.d("Firebase", "Owner: ${_selectedOwner.value}")
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            Log.e("Firebase", "Error: ${error.message}")
        }
    }

    fun selectOwner(uid: String) {
        viewModelScope.launch {
            when {
                _selectedOwner.value?.uid == uid -> { return@launch } // Do nothing if the sensor is already selected
                _selectedOwner.value?.uid != uid -> {
                    firebaseRepository.removeListener("$OWNERSHIPS_PATH/${_selectedOwner.value?.uid}", ownerListener)
                    firebaseRepository.addListener("$OWNERSHIPS_PATH/$uid", ownerListener)
                }
                else -> {
                    firebaseRepository.addListener("$OWNERSHIPS_PATH/$uid", ownerListener)
                }
            }
        }
    }

    fun unselectOwner() {
        viewModelScope.launch {
            firebaseRepository.removeListener("$OWNERSHIPS_PATH/${_selectedOwner.value?.uid}", ownerListener)
            _selectedOwner.update { null }
        }
    }

    fun addGuest(qrData: QRData) {
        val owner = qrData.uid
        val mac = qrData.mac
        val guest = _selectedOwner.value?.uid
        if (guest != null && owner.isNotBlank() && mac.isNotBlank()) {
            viewModelScope.launch {
                // Add guest to the owner's list
                val stringListTypeIndicator: GenericTypeIndicator<List<String>> =
                    object : GenericTypeIndicator<List<String>>() {}
                val guestList =
                    firebaseRepository.getData("$OWNERSHIPS_PATH/$owner/$mac/guests", stringListTypeIndicator)
                        ?.toMutableList() ?: mutableListOf()
                if (!guestList.contains(guest)) {
                    guestList.add(guest)
                    firebaseRepository.sendData(guestList, "$OWNERSHIPS_PATH/$owner/$mac/guests")
                }

                // Add device to the guest's list
                val ownershipListTypeIndicator: GenericTypeIndicator<List<Ownership>> =
                    object : GenericTypeIndicator<List<Ownership>>() {}
                val deviceList =
                    firebaseRepository.getData("$OWNERSHIPS_PATH/$guest", ownershipListTypeIndicator)
                        ?.toMutableList() ?: mutableListOf()
                val ownership = Ownership(null, mac, "guest")
                if (!deviceList.contains(ownership)) {
                    firebaseRepository.sendData(ownership, "$OWNERSHIPS_PATH/$guest/$mac")
                }
            }
        }
    }

    fun addOwner(owner: String, mac: String) {
        viewModelScope.launch {
            // Add device to the owner's list
            if (owner.isNotBlank() && mac.isNotBlank()) {
                val ownershipListTypeIndicator: GenericTypeIndicator<List<Ownership>> =
                    object : GenericTypeIndicator<List<Ownership>>() {}
                val deviceList =
                    firebaseRepository.getData("$OWNERSHIPS_PATH/$owner", ownershipListTypeIndicator)
                        ?.toMutableList() ?: mutableListOf()
                val ownership = Ownership(null, mac, "owner")
                if (!deviceList.contains(ownership)) {
                    firebaseRepository.sendData(ownership, "$OWNERSHIPS_PATH/$owner/$mac")
                }
            }
        }

    }

    fun removeGuest(owner: String, guest: String, mac: String) {
        viewModelScope.launch {
            // Remove guest from the owner's list
            val stringListTypeIndicator: GenericTypeIndicator<List<String>> =
                object : GenericTypeIndicator<List<String>>() {}
            val guestList =
                firebaseRepository.getData("$OWNERSHIPS_PATH/$owner/$mac/guests", stringListTypeIndicator)
                    ?.toMutableList() ?: mutableListOf()
            if (guestList.contains(guest)) {
                guestList.remove(guest)
                firebaseRepository.sendData(guestList, "$OWNERSHIPS_PATH/$owner/$mac/guests")
            }

            // Remove device from the guest's list
            val ownershipListTypeIndicator: GenericTypeIndicator<List<Ownership>> =
                object : GenericTypeIndicator<List<Ownership>>() {}
            val deviceList =
                firebaseRepository.getData("$OWNERSHIPS_PATH/$guest", ownershipListTypeIndicator)
                    ?.toMutableList() ?: mutableListOf()
            val ownership = Ownership(null, mac, "guest")
            if (deviceList.contains(ownership)) {
                firebaseRepository.removeData("$OWNERSHIPS_PATH/$guest/$mac")
            }
        }
    }

    private val guestsListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val guests = mutableListOf<String>()
            for (guest in snapshot.children) {
                try {
                    val data = guest.getValue(String::class.java)
                    if (data != null) {
                        guests.add(data)
                    }
                }
                catch (e: Exception) {
                    Log.e("Firebase", "Error: ${e.message}")
                }
            }
            _guests.update { guests }
            Log.d("Firebase", "Guests: ${_guests.value}")
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            Log.e("Firebase", "Error: ${error.message}")
        }
    }

    fun getGuestList(mac: String, owner: String) {
        viewModelScope.launch {
            Log.d("Firebase", "Adding listener for guests")
            firebaseRepository.addListener("$OWNERSHIPS_PATH/$owner/$mac/guests", guestsListener)
        }
    }

    fun removeGuestListListener(mac: String, owner: String) {
        viewModelScope.launch {
            Log.d("Firebase", "Removing listener for guests")
            firebaseRepository.removeListener("$OWNERSHIPS_PATH/$owner/$mac/guests", guestsListener)
            _guests.update { emptyList() }
        }
    }
}