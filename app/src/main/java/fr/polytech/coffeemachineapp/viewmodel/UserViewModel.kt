package fr.polytech.coffeemachineapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.GenericTypeIndicator
import fr.polytech.coffeemachineapp.model.User
import fr.polytech.coffeemachineapp.repository.FirebaseRepository
import fr.polytech.coffeemachineapp.utils.Constant.Companion.USERS_PATH
import kotlinx.coroutines.launch

class UserViewModel(private val firebaseRepository: FirebaseRepository): ViewModel() {
    suspend fun getUser(uid: String): User? {
        val typeData: GenericTypeIndicator<User> = object : GenericTypeIndicator<User>() {}
        val user = firebaseRepository.getData("$USERS_PATH/$uid", typeData)
        return user
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            if (user.uid.isNotEmpty()) {
                firebaseRepository.sendData(user, "$USERS_PATH/${user.uid}")
            }
        }
    }
}