package com.laperapp.laper

import android.content.Context
import com.laper.laperadmin.Model.*
import com.laper.laperadmin.api.LoginResponse
import com.laperapp.laper.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ResponseBodyApi {

    fun logInResponseBody(
        model: LoginModel,
        onResponse: (String?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val jsonapi = RetrofitClient.getClient()
        jsonapi.logIn(model).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    onResponse(response.body()?.token)
                } else {
                    onFailure(Throwable("Response unsuccessful"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun signUpResponseBody(
        model: SignUpModel,
        onResponse: (String?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val jsonapi = RetrofitClient.getClient()
        jsonapi.signUp(model).enqueue(object : Callback<SignUpModel> {
            override fun onResponse(call: Call<SignUpModel>, response: Response<SignUpModel>) {
                if (response.isSuccessful) {
                    onResponse("Sign Up Successful")
                } else {
                    onFailure(Throwable("Response unsuccessful"))
                }
            }

            override fun onFailure(call: Call<SignUpModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getExpertResponseBody(
        context: Context,
        onResponse: (ExpertBase?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val token = RetrofitClient.getCredential("token", context)
        val jsonapi = RetrofitClient.getClient()
        jsonapi.getExperts(token).enqueue(object : Callback<ExpertBase> {
            override fun onResponse(call: Call<ExpertBase>, response: Response<ExpertBase>) {
                if (response.isSuccessful) {
                    val userFetch: ExpertBase? = response.body()
                    onResponse(userFetch)
                } else {
                    onFailure(Throwable("Response unsuccessful"))
                }
            }

            override fun onFailure(call: Call<ExpertBase>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getUserResponseBody(
        context: Context,
        filterModel: FilterModel,
        onResponse: (UserBase?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val token = RetrofitClient.getCredential("token", context)
        val jsonapi = RetrofitClient.getClient()
        jsonapi.getExpertFetchUser(token, filterModel).enqueue(object : Callback<UserBase> {
            override fun onResponse(call: Call<UserBase>, response: Response<UserBase>) {
                if (response.isSuccessful) {
                    val userFetch: UserBase? = response.body()
                    onResponse(userFetch)
                } else {
                    onFailure(Throwable("Response unsuccessful"))
                }
            }

            override fun onFailure(call: Call<UserBase>, t: Throwable) {
                onFailure(t)
            }
        })
    }


    fun fetchRequest(
        filterModel: FilterModel,
        onResponse: (FetchRequestModel?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val jsonapi = RetrofitClient.getClient()
        jsonapi.fetchRequest(filterModel).enqueue(object : Callback<FetchRequestModel> {
            override fun onResponse(
                call: Call<FetchRequestModel>,
                response: Response<FetchRequestModel>
            ) {
                if (response.isSuccessful) {
                    val fetchRequestModel: FetchRequestModel? = response.body()
                    onResponse(fetchRequestModel)
                } else {
                    val errorMessage = "Response unsuccessful: ${response.code()}"
                    onFailure(Throwable(errorMessage))
                }
            }

            override fun onFailure(call: Call<FetchRequestModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun updateRequest(context:Context,
        updateReqModel: UpdateReqModel,
        onResponse: (Message?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val jsonapi = RetrofitClient.getClient()
        val token = RetrofitClient.getCredential("token", context)
        jsonapi.updateRequest(token,updateReqModel).enqueue(object : Callback<Message> {
            override fun onResponse(
                call: Call<Message>,
                response: Response<Message>
            ) {
                if (response.isSuccessful) {
                    val message: Message? = response.body()
                    onResponse(message)
                } else {
                    val errorMessage = "Response unsuccessful: ${response.code()}"
                    onFailure(Throwable(errorMessage))
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                onFailure(t)
            }
        })
    }


}