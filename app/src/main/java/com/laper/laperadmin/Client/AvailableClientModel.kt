package com.laper.laperadmin.Client

data class AvailableClientModel(
    val reqSentDate: Long,
    val expertId: String,
    val reqId: String,
    val expName: String,
    val expImage: String,
    val ps: String,
    val techId: ArrayList<String>,
    val clientId:String
)
