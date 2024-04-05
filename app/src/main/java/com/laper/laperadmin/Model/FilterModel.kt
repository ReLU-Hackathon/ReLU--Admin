package com.laper.laperadmin.Model

data class FilterModel (
    val field:String,
    val value:String,
    val sortField:String,
    val sort:Int=1,
    val lim:Int=0
)