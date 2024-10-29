package fr.polytech.coffeemachineapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.polytech.coffeemachineapp.model.Owner
import fr.polytech.coffeemachineapp.model.Ownership
import fr.polytech.coffeemachineapp.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OwnershipViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    private val ownershipPath = "ownership"
    private var _selectedOwner = MutableStateFlow<Owner?>(null)
    val selectedOwnerState: StateFlow<Owner?> = _selectedOwner

    private val ownerListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val userOwnership = mutableListOf<Ownership>()
            for (ownership in snapshot.children) {
                val data = ownership.getValue(Ownership::class.java)
                if (data != null) {
                    userOwnership.add(data)
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
                    firebaseRepository.removeListener("$ownershipPath/${_selectedOwner.value?.uid}", ownerListener)
                    firebaseRepository.addListener("$ownershipPath/$uid", ownerListener)
                }
                else -> {
                    firebaseRepository.addListener("$ownershipPath/$uid", ownerListener)
                }
            }
        }
    }

    fun unselectOwner() {
        viewModelScope.launch {
            firebaseRepository.removeListener("$ownershipPath/${_selectedOwner.value?.uid}", ownerListener)
            _selectedOwner.update { null }
        }
    }
}