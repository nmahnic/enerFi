package com.nicomahnic.dadm.enerfi.ui.fragments.espprovisioning

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.espressif.provisioning.DeviceConnectionEvent
import com.espressif.provisioning.ESPConstants
import com.espressif.provisioning.ESPProvisionManager
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.utils.AppConstants
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class WiFiConfigActivity : AppCompatActivity() {
    private val TAG = WiFiConfigActivity::class.java.simpleName

    private var tvTitle: TextView? = null
    private  var tvBack: TextView? = null
    private  var tvCancel: TextView? = null
    private var btnNext: CardView? = null
    private var txtNextBtn: TextView? = null

    private var etSsid: EditText? = null
    private var etPassword: EditText? = null
    private var provisionManager: ESPProvisionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_config)
        provisionManager = ESPProvisionManager.getInstance(applicationContext)
        initViews()
        EventBus.getDefault().register(this)
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
        val ssid = etSsid!!.text.toString()
        val password: String = etPassword!!.getText().toString()
        if (TextUtils.isEmpty(ssid)) {
            etSsid!!.error = getString(R.string.error_ssid_empty)
            return@OnClickListener
        }
        goToProvisionActivity(ssid, password)
    }

    private val cancelBtnClickListener = View.OnClickListener {
        provisionManager!!.espDevice.disconnectDevice()
        finish()
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.main_toolbar_title)
        tvBack = findViewById<TextView>(R.id.btn_back)
        tvCancel = findViewById<TextView>(R.id.btn_cancel)
        etSsid = findViewById(R.id.et_ssid_input)
        etPassword = findViewById<EditText>(R.id.et_password_input)
        val deviceName = provisionManager!!.espDevice.deviceName
        if (!TextUtils.isEmpty(deviceName)) {
            val msg = String.format(getString(R.string.setup_instructions), deviceName)
            val tvInstructionMsg = findViewById<TextView>(R.id.setup_instructions_view)
            tvInstructionMsg.text = msg
        }
        tvTitle!!.setText(R.string.title_activity_wifi_config)
        tvBack!!.setVisibility(View.GONE)
        tvCancel!!.setVisibility(View.VISIBLE)
        tvCancel!!.setOnClickListener(cancelBtnClickListener)
        btnNext = findViewById(R.id.btn_next)
        txtNextBtn = findViewById(R.id.text_btn)
        txtNextBtn!!.setText(R.string.btn_next)
        btnNext!!.setOnClickListener(nextBtnClickListener)
    }

    private fun goToProvisionActivity(ssid: String, password: String) {
        finish()
//        val provisionIntent = Intent(applicationContext, ProvisionActivity::class.java)
//        provisionIntent.putExtras(intent)
//        provisionIntent.putExtra(AppConstants.KEY_WIFI_SSID, ssid)
//        provisionIntent.putExtra(AppConstants.KEY_WIFI_PASSWORD, password)
//        startActivity(provisionIntent)
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