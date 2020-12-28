package com.example.recordcall


import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle


class MainActivity : Activity() {
    private var mDPM: DevicePolicyManager? = null
    private var mAdminName: ComponentName? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {

            mDPM = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
            mAdminName = ComponentName(this, DeviceAdminDemo::class.java)
            if (!mDPM!!.isAdminActive(mAdminName!!)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.")
                startActivityForResult(intent, REQUEST_CODE)
            } else {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE == requestCode) {
            val intent = Intent(this@MainActivity, TService::class.java)
            startService(intent)
        }
    }

    companion object {
        private const val REQUEST_CODE = 0
    }
}