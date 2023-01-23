package com.paxees.sms.network


import com.hbl.hblaccountopeningapp.network.models.response.base.SmsRequest
import retrofit2.Call
import retrofit2.http.*


interface APIInterface {
    @POST("shop/hari_msg_api.php")
    fun postSms(@Body request: SmsRequest?): Call<SmsResponse>

    companion object {
        const val HEADER_TAG = "@"
        const val HEADER_POSTFIX = ": "
        const val HEADER_TAG_PUBLIC = "public"
    }
}