package com.laperapp.laper.api

import com.laper.laperadmin.Model.*
import com.laper.laperadmin.api.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("api/experts/signup")
    fun signUp(@Body signUpRequest: SignUpModel): Call<SignUpModel>

    @POST("api/experts/login")
    fun logIn(@Body loginRequest: LoginModel): Call<LoginResponse>

    @POST("api/experts/user-fetch")
    fun getExperts(@Header("x-access-token") token:String,
    ):Call<ExpertBase>

    @POST("api/expert-fetch-users")
    fun getExpertFetchUser(@Header("x-access-token") token:String,
                           @Body filter: FilterModel):Call<UserBase>

    @POST("api/requests/fetch")
    fun fetchRequest(@Body filter: FilterModel): Call<FetchRequestModel>

    @PUT("api/requests/update")
    fun updateRequest(@Header("x-access-token") token:String,
                      @Body model:UpdateReqModel) : Call<Message>

}