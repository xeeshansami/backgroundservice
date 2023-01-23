package com.hbl.hblaccountopeningapp.network.ResponseHandlers.handler

import com.hbl.hblaccountopeningapp.network.ResponseHandlers.callbacks.SmsCallBack
import com.hbl.hblaccountopeningapp.network.models.response.base.BaseResponse
import com.paxees.sms.network.SmsResponse

class SmsBaseHR(callBack: SmsCallBack) : BaseRH<SmsResponse>() {
    var callback: SmsCallBack = callBack
    override fun onSuccess(response: SmsResponse?) {
        response?.let { callback.SmsSuccess(it) }
    }

    override fun onFailure(response: BaseResponse?) {
        response?.let { callback.SmsFailure(it) }
    }
}