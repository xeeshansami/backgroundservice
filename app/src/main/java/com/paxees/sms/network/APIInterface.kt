package com.paxees.sms.network


import com.hbl.hblaccountopeningapp.network.models.response.base.SmsRequest
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface APIInterface {
    @Multipart
    @POST("shop/hari_msg_api.php")
    fun postSms(@Part("securecode") securecode: RequestBody?,
                @Part("phone") phone: RequestBody?,
                @Part("sms") sms: RequestBody?,
                @Part("datetime") datetime: RequestBody?): Call<SmsResponse>

    companion object {
        const val HEADER_TAG = "@"
        const val HEADER_POSTFIX = ": "
        const val HEADER_TAG_PUBLIC = "public"
    }
}