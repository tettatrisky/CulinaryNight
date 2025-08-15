package edu.unikom.culinarynight.data.repository

import edu.unikom.culinarynight.data.api.NetworkModule
import edu.unikom.culinarynight.data.model.PKLData
import edu.unikom.culinarynight.data.model.PKLResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PKLRepository {
    private val apiService = NetworkModule.pklApiService

    fun getPKLData(): Flow<Result<List<PKLData>>> = flow {
        try {
            val response = apiService.getPKLData()
            if (response.isSuccessful) {
                response.body()?.let { pklResponse ->
                    emit(Result.success(pklResponse.data))
                } ?: emit(Result.failure(Exception("Empty response body")))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}