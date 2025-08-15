package edu.unikom.culinarynight.data.repository

import edu.unikom.culinarynight.data.api.NetworkModule
import edu.unikom.culinarynight.data.local.AppDatabase
import edu.unikom.culinarynight.data.local.entity.PKLEntity
import edu.unikom.culinarynight.data.model.PKLData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PKLRepository(private val db: AppDatabase) {
    private val apiService = NetworkModule.pklApiService
    private val dao = db.pklDao()

    fun getCachedPKL(): Flow<List<PKLData>> =
        dao.getAll().map { list -> list.map { it.toPKLData() } }

    fun getPKLData(): Flow<Result<List<PKLData>>> = flow {
        // emit cached first (if any)
        val cached = dao.getAll() // Flow
        // fetch from API
        try {
            val response = apiService.getPKLData()
            if (response.isSuccessful) {
                val data = response.body()?.data ?: emptyList()
                // cache
                dao.insertAll(data.map { pkl ->
                    PKLEntity(
                        lokasi = pkl.lokasi,
                        jenisUsaha = pkl.jenisUsaha,
                        jumlahPkl = pkl.jumlahPkl,
                        satuan = pkl.satuan,
                        tahun = pkl.tahun,
                        latitude = pkl.latitude,
                        longitude = pkl.longitude
                    )
                })
                emit(Result.success(data))
            } else {
                emit(Result.failure(Exception("API Error ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
