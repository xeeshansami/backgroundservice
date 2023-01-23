package com.paxees.sms.network

import android.app.Application
import com.hbl.hblaccountopeningapp.network.ResponseHandlers.callbacks.SmsCallBack
import com.hbl.hblaccountopeningapp.network.ResponseHandlers.handler.SmsBaseHR
import com.hbl.hblaccountopeningapp.network.enums.RetrofitEnums
import com.hbl.hblaccountopeningapp.network.models.response.base.SmsRequest
import com.hbl.hblaccountopeningapp.network.retrofitBuilder.RetrofitBuilder
import com.paxees.sms.network.timeoutInterface.IOnConnectionTimeoutListener

open class ApiStore : Application(), IOnConnectionTimeoutListener {

    override fun onConnectionTimeout() {}

    companion object {
        private val consumerStore: ApiStore? = null
        val instance: ApiStore?
            get() = consumerStore ?: ApiStore()
    }

    //:TODO post getLogin
    fun postSms(
        url: RetrofitEnums?,
        request: SmsRequest?,
        callBack: SmsCallBack
    ) {
        var privateInstanceRetrofit: APIInterface? =
            GlobalClass.applicationContext?.let { RetrofitBuilder.getRetrofitInstance(it,    url!!, Config.API_CONNECT_TIMEOUT) }
        privateInstanceRetrofit?.postSms(request)!!.enqueue(SmsBaseHR(callBack))
    }

}