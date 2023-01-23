package com.paxees.sms.network

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*
class GlobalClass : ApiStore() {
    override fun onCreate() {
        super.onCreate()
        Companion.applicationContext = applicationContext
    }
    companion object{
        @JvmField
        var applicationContext: Context? = null
        var sourceCode="8181kdj8272hdhd72"
        val currentDate: String
            get() {
                val sdf = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
                return sdf.format(Date())
            }
    }


}