package br.edu.ifsp.scl.sdm.forca.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ForcaApi {
    @GET("identificadores/{id}")
    fun retrieveIdentificadores(@Path("id") id: Int): Call<Array<Int>>

    @GET("palavra/{id}")
    fun retrievePalavras(@Path("id") id: Int): Call<Array<Palavras>>
}


