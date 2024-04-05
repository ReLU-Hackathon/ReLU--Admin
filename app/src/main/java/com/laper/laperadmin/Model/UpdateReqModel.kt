package com.laper.laperadmin.Model

import java.util.Objects

data class UpdateReqModel(
    val requestId: String,
    val field: String,
    val value: Any
)
