package com.example.recordcall

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.util.*


class TService : Service() {
    var recorder: MediaRecorder? = null
    var audiofile: File? = null

    var recordstarted = false
    var brcall: CallBr? = null
    override fun onBind(arg0: Intent): IBinder? {

        return null
    }

    override fun onDestroy() {
        Log.d("service", "destroy")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val filter = IntentFilter()
        filter.addAction(ACTION_OUT)
        filter.addAction(ACTION_IN)
        brcall = CallBr()
        this.registerReceiver(brcall, filter)


        return START_NOT_STICKY
    }

    inner class CallBr : BroadcastReceiver() {
        var bundle: Bundle? = null
        var state: String? = null
        var inCall: String? = null
        var outCall: String? = null
        var wasRinging = false
        @SuppressLint("NewApi", "SimpleDateFormat")
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_IN) {
                if (intent.extras.also { bundle = it } != null) {
                    state = bundle!!.getString(TelephonyManager.EXTRA_STATE)
                    if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                        inCall = bundle!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                        wasRinging = true
                        Toast.makeText(context, "IN : $inCall", Toast.LENGTH_LONG).show()
                    } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                        if (wasRinging) {
                            Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show()
                            SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(Date())
                            val sampleDir = File(Environment.getExternalStorageDirectory(), "/TestRecordingDasa1")
                            if (!sampleDir.exists()) {
                                sampleDir.mkdirs()
                            }
                            val filename = "Record"
                            try {
                                audiofile = File.createTempFile(filename, ".amr", sampleDir)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            Environment.getExternalStorageDirectory().absolutePath
                            recorder = MediaRecorder()
                            recorder!!.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
                            recorder!!.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK)
                            recorder!!.setAudioSource(MediaRecorder.AudioSource.VOICE_UPLINK)
                            recorder!!.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                            recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                            recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                            recorder!!.setOutputFile(audiofile!!.absolutePath)
                            try {
                                recorder!!.prepare()
                            } catch (e: IllegalStateException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            recorder!!.start()
                            recordstarted = true
                        }
                    } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                        wasRinging = false
                        Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show()
                        if (recordstarted) {
                            recorder!!.stop()
                            recordstarted = false
                        }
                    }
                }
            } else if (intent.action == ACTION_OUT) {
                if (intent.extras.also { bundle = it } != null) {
                    outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                    Toast.makeText(context, "OUT : $outCall", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        private const val ACTION_IN = "android.intent.action.PHONE_STATE"
        private const val ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL"
    }
}