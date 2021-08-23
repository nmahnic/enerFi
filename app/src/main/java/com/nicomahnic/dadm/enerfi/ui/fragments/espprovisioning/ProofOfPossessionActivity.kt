package com.nicomahnic.dadm.enerfi.ui.fragments.espprovisioning

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View

import androidx.appcompat.app.AlertDialog
import com.espressif.provisioning.DeviceConnectionEvent
import com.espressif.provisioning.ESPConstants
import com.espressif.provisioning.ESPProvisionManager
import com.nicomahnic.dadm.enerfi.R
import kotlinx.android.synthetic.main.activity_proof_of_possession.*
import kotlinx.android.synthetic.main.button.*
import kotlinx.android.synthetic.main.tool_bar_main_purple.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProofOfPossessionActivity : AppCompatActivity() {
    private val TAG = ProofOfPossessionActivity::class.java.simpleName

    private var deviceName: String? = null
    private var provisionManager: ESPProvisionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proof_of_possession)
        provisionManager = ESPProvisionManager.getInstance(applicationContext)
        initViews()
        EventBus.getDefault().register(this)

        deviceName = provisionManager!!.espDevice.deviceName
        if (!TextUtils.isEmpty(deviceName)) {
            val popText = getString(R.string.pop_instruction) + " " + deviceName
            tv_pop!!.text = popText
        }
        val pop = resources.getString(R.string.proof_of_possesion)
        if (!TextUtils.isEmpty(pop)) {
            et_pop!!.setText(pop)
            et_pop!!.setSelection(et_pop!!.text.length)
        }
        et_pop!!.requestFocus()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        provisionManager!!.espDevice.disconnectDevice()
        super.onBackPressed()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DeviceConnectionEvent) {
        Log.d(TAG, "On Device Connection Event RECEIVED : " + event.eventType)
        when (event.eventType) {
            ESPConstants.EVENT_DEVICE_DISCONNECTED -> if (!isFinishing) {
                showAlertForDeviceDisconnected()
            }
        }
    }

    private val nextBtnClickListener = View.OnClickListener {
        val pop = et_pop!!.text.toString()
        Log.d(TAG, "POP : $pop")
        provisionManager!!.espDevice.proofOfPossession = pop
        val deviceCaps = provisionManager!!.espDevice.deviceCapabilities
        if (deviceCaps.contains("wifi_scan")) {
            goToWiFiScanListActivity()
        } else {
            goToWiFiConfigActivity()
        }
    }

    private val cancelBtnClickListener = View.OnClickListener {
        provisionManager!!.espDevice.disconnectDevice()
        finish()
    }

    private fun initViews() {
        main_toolbar_title!!.setText(R.string.title_activity_pop)
        btn_back!!.setVisibility(View.GONE)
        btn_cancel!!.setVisibility(View.VISIBLE)
        btn_cancel!!.setOnClickListener(cancelBtnClickListener)
        text_btn!!.setText(R.string.btn_next)
        btn_next!!.setOnClickListener(nextBtnClickListener)
    }

    private fun goToWiFiScanListActivity() {
        val wifiListIntent = Intent(applicationContext, WiFiScanActivity::class.java)
        wifiListIntent.putExtras(intent)
        startActivity(wifiListIntent)
        finish()
    }

    private fun goToWiFiConfigActivity() {
        val wifiConfigIntent = Intent(applicationContext, WiFiConfigActivity::class.java)
        wifiConfigIntent.putExtras(intent)
        startActivity(wifiConfigIntent)
        finish()
    }

    private fun showAlertForDeviceDisconnected() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(R.string.error_title)
        builder.setMessage(R.string.dialog_msg_ble_device_disconnection)

        // Set up the buttons
        builder.setPositiveButton(R.string.btn_ok,
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                finish()
            })
        builder.show()
    }
}