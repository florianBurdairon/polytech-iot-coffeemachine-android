package fr.polytech.coffeemachineapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.polytech.coffeemachineapp.model.Request
import fr.polytech.coffeemachineapp.model.RequestRaw
import fr.polytech.coffeemachineapp.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RequestViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    private val requestsPath = "requests"

    private var _currentRequest = MutableStateFlow<Request?>(null)
    val currentRequest : StateFlow<Request?> = _currentRequest.asStateFlow()
    private var _nextRequest = MutableStateFlow<Request?>(null)
    val nextRequest : StateFlow<Request?> = _nextRequest.asStateFlow()
    private var _requests = MutableStateFlow(listOf<Request>())
    val requests : StateFlow<List<Request>> = _requests.asStateFlow()

    private val requestsListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot.children.forEach { deviceSnapshot ->
                when (deviceSnapshot.key) {
                    "current" -> {
                        Log.d("Firebase", "Current request: ${deviceSnapshot.value}")
                        if (deviceSnapshot.value == "null") {
                            _currentRequest.update { null }
                        }
                        else {
                            val currentRequestRaw = deviceSnapshot.getValue(RequestRaw::class.java)
                            val currentRequest = currentRequestRaw?.let { Request(it.mac, it.uid, it.action, it.status, it.timestamp.toLong()) }
                            currentRequest?.let { _currentRequest.update { currentRequest } }
                        }
                    }
                    "next" -> {
                        Log.d("Firebase", "Next request: ${deviceSnapshot.value}")
                        if (deviceSnapshot.value == "null") {
                            _nextRequest.update { null }
                        }
                        else {
                            val nextRequestRaw = deviceSnapshot.getValue(RequestRaw::class.java)
                            val nextRequest = nextRequestRaw?.let { Request(it.mac, it.uid, it.action, it.status, it.timestamp.toLong()) }
                            nextRequest?.let { _currentRequest.update { nextRequest } }
                        }
                    }
                    "list" -> {
                        val requestList = mutableListOf<Request>()
                        deviceSnapshot.children.forEach { requestSnapshot ->
                            val request = requestSnapshot.getValue(Request::class.java)
                            request?.let { requestList.add(it) }
                        }
                        requestList.sortBy { it.timestamp }
                        _requests.update { requestList }
                    }
                    else -> {
                        Log.e("Firebase", "Unknown key: ${deviceSnapshot.key}")
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Error: ${error.message}")
        }
    }

    fun addRequestsListener(mac: String) {
        viewModelScope.launch {
            firebaseRepository.addListener("$requestsPath/$mac", requestsListener)
        }
    }

    fun removeRequestsListener(mac: String) {
        viewModelScope.launch {
            firebaseRepository.removeListener("$requestsPath/$mac", requestsListener)
        }
    }

    fun setCurrentRequest(request: Request) {
        viewModelScope.launch {
            firebaseRepository.sendData(request, "$requestsPath/${request.mac}/current")
            _currentRequest.update { request }
        }
    }

    fun addRequest(request: Request) {
        viewModelScope.launch {
            // If new request is before next request, replace next request  by new request and add the old next request to the list of requests
            if (nextRequest.value?.let { request.timestamp < it.timestamp } != false) {
                val oldNextRequest = nextRequest.value
                firebaseRepository.sendData(request, "$requestsPath/${request.mac}/next")
                oldNextRequest?.let { firebaseRepository.sendData(it, "$requestsPath/${request.mac}/list/${it.timestamp}") }
            }
            // If new request is after next request, add it to the list of requests
            else {
                firebaseRepository.sendData(request, "$requestsPath/${request.mac}/list/${request.timestamp}")
            }
        }
    }

    fun removeRequest(request: Request) {
        viewModelScope.launch {
            if (request == nextRequest.value) {
                val next = requests.value.minByOrNull { it.timestamp }
                next?.let { firebaseRepository.sendData(it, "$requestsPath/${request.mac}/next") } ?:
                    firebaseRepository.sendData("null", "$requestsPath/${request.mac}/next")
            }
            else {
                firebaseRepository.removeData("$requestsPath/${request.mac}/list/${request.timestamp}")
            }
        }
    }
}