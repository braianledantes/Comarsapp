package com.wonder.comarsapp

import com.google.gson.annotations.SerializedName

data class RemitoResponse(
    @SerializedName("puntoVenta") val puntoVenta: Int,
    @SerializedName("nroRemito") val nroRemito: Int,
    @SerializedName("sector") val sector: String,
    @SerializedName("descargado") val descargado: Int
)
