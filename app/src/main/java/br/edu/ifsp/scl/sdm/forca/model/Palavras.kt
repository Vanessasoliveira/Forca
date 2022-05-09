package br.edu.ifsp.scl.sdm.forca.model

import com.google.gson.annotations.SerializedName

class Palavras (

    @SerializedName("Id")
    val id: Int,
    @SerializedName("Palavra")
    val palavras: String ,
    @SerializedName("Letras")
    val letra: Int,
    @SerializedName("Nivel")
    val nivel: Int

    )

