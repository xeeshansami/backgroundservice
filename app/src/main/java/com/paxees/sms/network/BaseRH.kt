package com.hbl.hblaccountopeningapp.network.ResponseHandlers.handler

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.hbl.hblaccountopeningapp.network.models.response.base.BaseResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


abstract class BaseRH<T> : Callback<T> {
    var message = ""
    @SuppressLint("WrongConstant")
    override fun onResponse(call: Call<T>, response: Response<T>) {
        try {
            if (response.isSuccessful) {//200
                val body = response.body()
                var baseResponse = Gson().toJson(body)
                var jsonResponse = JSONObject(baseResponse)
                onSuccess(body)
            }else{
                onFailure(call,Throwable())
            }
        } catch (ex: Exception) {

        }

    }


    override fun onFailure(call: Call<T>, t: Throwable) {
        t.message?.let { Log.i("JsonError", it) }
        if (t is SocketTimeoutException) { /*When server no response in 30 seconds*/
            onFailure(
                BaseResponse(
                    "-1",
                    "Connection timeout, kindly make sure your network connection should be worked fine and please try again"
                )
            )
        } else if (t.message!!.contains(" Unable to resolve")) {
            onFailure(
                BaseResponse(
                    "-1",
                    "Internet Connectivity Issue, kindly make sure your network connection should be worked fine and please try again"
                )
            )
        } else { /*When something unexpected error occurred.*/
            onFailure(
                BaseResponse(
                    "-1",
                    "Something went wrong, please try again later."
                )
            )
        }
    }

    protected abstract fun onSuccess(response: T?)
    protected abstract fun onFailure(response: BaseResponse?)
}