package edu.unikom.culinarynight.data.api

import edu.unikom.culinarynight.data.model.PKLResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PKLApiService {
    @GET("jumlah_penataan_pkl_di_kota_bandung")
    suspend fun getPKLData(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Response<PKLResponse>
}