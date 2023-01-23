package com.hbl.hblaccountopeningapp.network.ResponseHandlers.callbacks

import com.hbl.hblaccountopeningapp.network.models.response.base.*
import com.paxees.sms.network.SmsResponse
import retrofit2.Callback

interface SmsCallBack {
    fun SmsSuccess(response: SmsResponse)
    fun SmsFailure(response: BaseResponse)
}