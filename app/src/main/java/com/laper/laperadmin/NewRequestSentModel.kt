package com.lapperapp.laper.ui.NewDashboard.NewRequest

data class NewRequestSentModel(
    val id:String,
    val reqSentDate: String,
    val expertId: String,
    val reqId: String,
    val ps: String,
    val techId: ArrayList<String>
)
