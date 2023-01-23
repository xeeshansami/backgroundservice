package com.paxees.sms

import android.Manifest.permission.*
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    val RequestPermissionCode = 1
    var mServiceIntent: Intent? = null
    private var mYourService: YourService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permission()
    }

    fun permission(){
        startService()
       if(!checkPermission()) {
           ActivityCompat.requestPermissions(
               this@MainActivity, arrayOf(
                   READ_SMS,
                   INTERNET,
                   READ_EXTERNAL_STORAGE
               ), RequestPermissionCode
           )
       }else{
           startService()
       }
    }
    fun checkPermission(): Boolean {
        val FirstPermissionResult = ContextCompat.checkSelfPermission(applicationContext, INTERNET)
        val SecondPermissionResult =
            ContextCompat.checkSelfPermission(applicationContext, READ_SMS)
        val ThirdPermissionResult =
            ContextCompat.checkSelfPermission(applicationContext, READ_EXTERNAL_STORAGE)
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED && SecondPermissionResult == PackageManager.PERMISSION_GRANTED && ThirdPermissionResult == PackageManager.PERMISSION_GRANTED
    }

    fun startService(){
        mYourService = YourService()
        mServiceIntent = Intent(this, mYourService!!.javaClass)
        if (!isMyServiceRunning(mYourService!!.javaClass)) {
            startService(mServiceIntent)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestPermissionCode -> if (grantResults.size > 0) {
                val smsPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val internetPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val readExternalPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED
                if (smsPermission && internetPermission && readExternalPermission) {
                    startService()
                } else {
                    Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
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