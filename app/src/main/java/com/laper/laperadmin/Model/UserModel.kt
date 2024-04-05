package com.laper.laperadmin.Model

data class UserModel(
    val _id:Object,
    val date_created: String,
    val userId:String,
    val email: String,
    val username: String,
    val name: String,
    val userImageUrl: String,
    val lastActive: String,
    val desc: String,
    val phoneNumber: String,
    val userType: String,
    val versionCode: Int?,
    val versionName: String?,
    val __v: Int
)
