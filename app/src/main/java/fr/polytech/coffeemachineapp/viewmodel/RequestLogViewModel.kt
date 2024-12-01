package fr.polytech.coffeemachineapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.polytech.coffeemachineapp.model.RequestLog
import fr.polytech.coffeemachineapp.model.RequestLogRaw
import fr.polytech.coffeemachineapp.repository.FirebaseRepository
import fr.polytech.coffeemachineapp.utils.Constant.Companion.REQUEST_LOGS_PATH
import fr.polytech.coffeemachineapp.utils.LogStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RequestLogViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    private var _requestLogs = MutableStateFlow(listOf<RequestLog>())
    val requestLogs = _requestLogs.asStateFlow()

    private inner class RequestLogListener(private val requestLogs: MutableStateFlow<List<RequestLog>>, val mac: String) : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val logsList = mutableListOf<RequestLog>()
            dataSnapshot.children.forEach { deviceSnapshot ->
                val requestLog = deviceSnapshot.getValue(RequestLogRaw::class.java)
                if (requestLog?.mac == mac) {
                    logsList.add(
                        RequestLog(
                            requestLog.mac,
                            requestLog.uid,
                            requestLog.action,
                            LogStatus.valueOf(requestLog.status),
                            requestLog.timestamp
                        )
                    )
                    requestLogs.update { logsList }
                }
            }
            Log.d("Firebase", "Request logs: ${requestLogs.value.size}")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Error: ${error.message}")
        }
    }

    private inner class AllRequestLogListener(private val requestLogs: MutableStateFlow<List<RequestLog>>) : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val logsList = mutableListOf<RequestLog>()
            dataSnapshot.children.forEach { deviceSnapshot ->
                deviceSnapshot.children.forEach { logsForUserSnapshot ->
                    val requestLog = logsForUserSnapshot.getValue(RequestLogRaw::class.java)
                    if (requestLog != null) {
                        logsList.add(
                            RequestLog(
                                requestLog.mac,
                                requestLog.uid,
                                requestLog.action,
                                LogStatus.valueOf(requestLog.status),
                                requestLog.timestamp
                            )
                        )
                        requestLogs.update { logsList }
                    }
                }
            }
            Log.d("Firebase", "All request logs: ${requestLogs.value.size}")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Error: ${error.message}")
        }
    }

    private var listenerMap = mutableMapOf<String, ValueEventListener>()

    fun addRequestLogListener(uid: String, mac: String) {
        val requestLogsListener = RequestLogListener(_requestLogs, mac)
        listenerMap[mac] = requestLogsListener
        listenerMap[mac]?.let { firebaseRepository.addListener("$REQUEST_LOGS_PATH/$uid", it) }
    }

    fun removeRequestLogListener(uid: String, mac: String) {
        listenerMap[mac]?.let { firebaseRepository.removeListener("$REQUEST_LOGS_PATH/$uid", it) }
        listenerMap.remove(mac)
    }

    fun addAllRequestLogListener() {
        val allRequestLogsListener = AllRequestLogListener(_requestLogs)
        listenerMap["all"] = allRequestLogsListener
        listenerMap["all"]?.let { firebaseRepository.addListener(REQUEST_LOGS_PATH, it) }
    }

    fun removeAllRequestLogListener() {
        listenerMap["all"]?.let { firebaseRepository.removeListener(REQUEST_LOGS_PATH, it) }
        listenerMap.remove("all")
    }

    fun addRequestLog(requestLog: RequestLog) {
        viewModelScope.launch {
            firebaseRepository.sendData(
                requestLog,
                "$REQUEST_LOGS_PATH/${requestLog.uid}/${requestLog.timestamp}"
            )
        }
    }

    fun removeRequestLog(requestLog: RequestLog) {
        viewModelScope.launch {
            firebaseRepository.removeData("$REQUEST_LOGS_PATH/${requestLog.uid}/${requestLog.timestamp}")
        }
    }
}