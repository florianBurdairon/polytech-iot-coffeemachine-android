package fr.polytech.coffeemachineapp.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

interface FirebaseRepository {
    suspend fun sendData(data: Any, path: String)
    suspend fun <T> getData(path: String, dataType: GenericTypeIndicator<T>): T?
    suspend fun <T> getDataRaw(path: String): T?
    suspend fun removeData(path: String)
    fun addListener(path: String, listener: ValueEventListener)
    fun removeListener(path: String, listener: ValueEventListener)
}

class FirebaseRepositoryImpl(database: FirebaseDatabase) : FirebaseRepository {
    private val dataRef = database.reference

    override suspend fun sendData(data: Any, path: String) {
        try {
            dataRef.child(path).setValue(data).await()
        } catch (e: Exception) {
            // Handle error (e.g., log, throw custom exception)
            Log.e("Firebase", "Error sending data: ${e.message}", e)
        }
    }

    override suspend fun <T> getData(path: String, dataType: GenericTypeIndicator<T>): T? {
        return try {
            val snapshot = dataRef.child(path).get().await()
            snapshot.getValue(dataType)
        } catch (e: Exception) {
            // Handle error (e.g., log, return null)
            Log.e("Firebase", "Error getting data: ${e.message}", e)
            null
        }
    }

    override suspend fun <T> getDataRaw(path: String): T? {
        return try {
            val snapshot = dataRef.child(path).get().await()
            snapshot.value as T
        } catch (e: Exception) {
            // Handle error (e.g., log, return null)
            Log.e("Firebase", "Error getting data: ${e.message}", e)
            null
        }
    }

    override suspend fun removeData(path: String) {
        try {
            dataRef.child(path).removeValue().await()
        } catch (e: Exception) {
            // Handle error (e.g., log, throw custom exception)
            Log.e("Firebase", "Error removing data: ${e.message}", e)
        }
    }

    override fun addListener(path: String, listener: ValueEventListener) {
        dataRef.child(path).addValueEventListener(listener)
    }

    override fun removeListener(path: String, listener: ValueEventListener) {
        dataRef.child(path).removeEventListener(listener)
    }
}