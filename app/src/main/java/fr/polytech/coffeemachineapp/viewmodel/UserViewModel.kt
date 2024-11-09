package fr.polytech.coffeemachineapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.GenericTypeIndicator
import fr.polytech.coffeemachineapp.model.User
import fr.polytech.coffeemachineapp.repository.FirebaseRepository
import kotlinx.coroutines.launch

class UserViewModel(private val firebaseRepository: FirebaseRepository): ViewModel() {
    private val userPath = "users"

    suspend fun getUser(uid: String): User? {
        val typeData: GenericTypeIndicator<User> = object : GenericTypeIndicator<User>() {}
        val user = firebaseRepository.getData("$userPath/$uid", typeData)
        return user
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            firebaseRepository.sendData(user, "$userPath/${user.uid}")
        }
    }
}