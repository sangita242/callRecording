package com.example.recordcall

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent


class DeviceAdminDemo : DeviceAdminReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        context.stopService(Intent(context, TService::class.java))
        val myIntent = Intent(context, TService::class.java)
        context.startService(myIntent)
    }

    override fun onEnabled(context: Context, intent: Intent) {

    }

    override fun onDisabled(context: Context, intent: Intent) {

    }



}