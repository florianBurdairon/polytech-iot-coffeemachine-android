package fr.polytech.coffeemachineapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.polytech.coffeemachineapp.model.RequestLog
import fr.polytech.coffeemachineapp.repository.FirebaseRepository
import fr.polytech.coffeemachineapp.utils.Constant.Companion.REQUEST_LOGS_PATH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RequestLogViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    private var _requestLogs = MutableStateFlow(listOf<RequestLog>())
    val requestLogs = _requestLogs.asStateFlow()

    private val requestLogsListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot.children.forEach { deviceSnapshot ->
                val requestLog = deviceSnapshot.getValue(RequestLog::class.java)
                requestLog?.let { _requestLogs.update { requestLogs.value + it } }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Error: ${error.message}")
        }
    }

    fun addRequestLogListener(uid: String) {
        firebaseRepository.addListener("$REQUEST_LOGS_PATH/$uid", requestLogsListener)
    }

    fun removeRequestLogListener(uid: String) {
        firebaseRepository.removeListener("$REQUEST_LOGS_PATH/$uid", requestLogsListener)
    }

    fun addRequestLog(requestLog: RequestLog) {
        viewModelScope.launch {
            firebaseRepository.sendData(
                requestLog,
                "$REQUEST_LOGS_PATH/${requestLog.uid}/${requestLog.timeStamp}"
            )
        }
    }

    fun removeRequestLog(requestLog: RequestLog) {
        viewModelScope.launch {
            firebaseRepository.removeData("$REQUEST_LOGS_PATH/${requestLog.uid}/${requestLog.timeStamp}")
        }
    }
}