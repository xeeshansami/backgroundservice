package com.paxees.sms

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.hbl.hblaccountopeningapp.network.ResponseHandlers.callbacks.SmsCallBack
import com.hbl.hblaccountopeningapp.network.enums.RetrofitEnums
import com.hbl.hblaccountopeningapp.network.models.response.base.BaseResponse
import com.hbl.hblaccountopeningapp.network.models.response.base.SmsRequest
import com.paxees.sms.network.ApiStore
import com.paxees.sms.network.GlobalClass
import com.paxees.sms.network.SmsResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.util.*

class Restarter : BroadcastReceiver() {
    private var count = 0;

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("BackServices", "BroadCast Reciever hit")

        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            var msgs: Array<SmsMessage?>? = null
            var msg = ""
            var number = ""
            var date = ""
            if (bundle != null) {
                val pdus = bundle["pdus"] as Array<Any>?
                msgs = arrayOfNulls(pdus!!.size)
                for (i in msgs.indices) {
                    msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    msg += msgs[i]!!.messageBody
                    number = msgs[i]!!.displayOriginatingAddress
                    date = millisToDate(msgs[i]!!.timestampMillis)!!
                }
                Log.i("BackServices", "Msg Got\nMsg= $msg\nNumber= $number\nDate= $date")
                postSms(
                    msg,
                    number,
                    date
                )
            }
        }
    }

    fun postSms(msg: String, number: String, dateTime: String) {
        var request = SmsRequest()
        request.datetime = dateTime
        request.securecode = GlobalClass.sourceCode
        request.phone = number
        request.sms = msg
        val dateTimeValue: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            dateTime
        )
        val secureCodeValue: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            GlobalClass.sourceCode
        )
        val numberValue: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            number
        )
        val sms: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            msg
        )

        Log.i("BackServices", "Request" + Gson().toJson(request))
        ApiStore.instance?.postSms(
            RetrofitEnums.URL_HBL,
            secureCodeValue, numberValue, sms, dateTimeValue,
            object : SmsCallBack {
                override fun SmsSuccess(response: SmsResponse) {
                    Log.i("BackServices", "Single sms send to server" + Gson().toJson(response))
                }

                override fun SmsFailure(response: BaseResponse) {
                    Log.i("BackServices", Gson().toJson(response))
                }
            })
    }


    companion object {
        private const val TAG = "SmsBroadcastReceiver"
        const val pdu_type = "pdus"
    }

    fun millisToDate(currentTime: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        val sdf = SimpleDateFormat("hh:mm:ss dd:MM:yyyy")
        var dateData = sdf.format(calendar.time).toString()
        return dateData
    }
}