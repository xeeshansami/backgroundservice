package com.paxees.sms

import android.Manifest
import android.Manifest.permission.*
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.paxees.sms.permission.PermissionHandler
import com.paxees.sms.permission.Permissions


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    var mServiceIntent: Intent? = null
    var permissions = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.INTERNET,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private var mYourService: YourService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestReadAndSendSmsPermission()
    }

    private fun requestReadAndSendSmsPermission() {
        Permissions.check(
            this@MainActivity /*context*/,
            permissions,
            null /*rationale*/,
            null /*options*/,
            object : PermissionHandler() {
                override fun onGranted() {
                    // do your task.
//                    startBroadCast()
                    mYourService = YourService()
                    mServiceIntent = Intent(this@MainActivity, mYourService!!.javaClass)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!isMyServiceRunning(mYourService!!.javaClass)) {
                            Log.i(TAG,"ServiceStarted")
                            startForegroundService(mServiceIntent)
                        }
                    } else {
                        if (!isMyServiceRunning(mYourService!!.javaClass)) {
                            Log.i(TAG,"ServiceStarted")
                            startService(mServiceIntent)
                        }
                    }
                }
            })

    }
    fun startBroadCast(){
        val filter = IntentFilter()
        filter.addAction("restartservice")
        filter.priority = 2147483647
        val receiver = Restarter()
        registerReceiver(receiver, filter)
//
//
//        val broadcastIntent = Intent()
//        broadcastIntent.action = "restartservice"
//        broadcastIntent.setClass(this, Restarter::class.java)
//        this.sendBroadcast(broadcastIntent)
    }

    fun startService(){
        mYourService = YourService()
        mServiceIntent = Intent(this, mYourService!!.javaClass)
        if (!isMyServiceRunning(mYourService!!.javaClass)) {
            Log.i(TAG,"ServiceStarted")
            startService(mServiceIntent)
        }
    }


    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }

    override fun onDestroy() {
        //stopService(mServiceIntent);
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
    }
}