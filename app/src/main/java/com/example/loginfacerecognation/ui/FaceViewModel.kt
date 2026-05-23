package com.example.loginfacerecognation.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginfacerecognation.data.AppDatabase
import com.example.loginfacerecognation.data.FaceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.math.sqrt

class FaceViewModel(application: Application) : AndroidViewModel(application) {
    private val faceDao = AppDatabase.getDatabase(application).faceDao()

    suspend fun registerFace(name: String, embedding: FloatArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val embeddingString = embedding.joinToString(",")
                faceDao.insertFace(FaceEntity(name = name, embedding = embeddingString))
                Log.d("FaceViewModel", "SUCCESS: Face saved for $name")
                true
            } catch (e: Exception) {
                Log.e("FaceViewModel", "ERROR: Failed to save face", e)
                false
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            faceDao.deleteAllFaces()
            Log.d("FaceViewModel", "SUCCESS: All data cleared")
        }
    }

    suspend fun verifyFace(currentEmbedding: FloatArray): Boolean {
        return withContext(Dispatchers.IO) {
            val registeredFaces = faceDao.getAllFaces()
            Log.d("FaceViewModel", "Checking login against ${registeredFaces.size} users")
            
            for (face in registeredFaces) {
                val registeredEmbedding = face.embedding.split(",").map { it.toFloat() }.toFloatArray()
                val distance = calculateDistance(currentEmbedding, registeredEmbedding)
                Log.d("FaceViewModel", "Distance to user ${face.id}: $distance")

                if (distance < 0.7) {
                    return@withContext true
                }
            }
            false
        }
    }

    private fun calculateDistance(emb1: FloatArray, emb2: FloatArray): Float {
        var sum = 0.0f
        for (i in emb1.indices) {
            sum += (emb1[i] - emb2[i]).pow(2)
        }
        return sqrt(sum)
    }
}
