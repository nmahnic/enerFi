package com.nicomahnic.dadm.enerfi.ui.fragments.espprovisioning

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.widget.ContentLoadingProgressBar
import com.espressif.provisioning.DeviceConnectionEvent
import com.espressif.provisioning.ESPConstants
import com.espressif.provisioning.ESPProvisionManager
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.utils.AppConstants

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ProvisionLanding : AppCompatActivity() {
    private var tvTitle: TextView? = null
    private var tvBack: TextView? = null
    private var tvCancel: TextView? = null
    private var btnConnect: CardView? = null
    private var txtConnectBtn: TextView? = null
    private var arrowImage: ImageView? = null
    private var progressBar: ContentLoadingProgressBar? = null
    private var tvConnectDeviceInstruction: TextView? = null
    private var tvDeviceName: TextView? = null
    private var provisionManager: ESPProvisionManager? = null
    private var securityType = 0
    private var deviceName: String? = null
    private var pop: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provision_landing)
        securityType = intent.getIntExtra(AppConstants.KEY_SECURITY_TYPE, 0)
        deviceName = intent.getStringExtra(AppConstants.KEY_DEVICE_NAME)
        pop = intent.getStringExtra(AppConstants.KEY_PROOF_OF_POSSESSION)
        provisionManager = ESPProvisionManager.getInstance(applicationContext)
        initViews()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (provisionManager!!.espDevice != null) {
            provisionManager!!.espDevice.disconnectDevice()
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            WIFI_SETTINGS_ACTIVITY_REQUEST -> {
                if (hasPermissions()) {
                    connectDevice()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_FINE_LOCATION -> {
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DeviceConnectionEvent) {
        Log.d(TAG, "On Device Prov Event RECEIVED : " + event.eventType)
        when (event.eventType) {
            ESPConstants.EVENT_DEVICE_CONNECTED -> {
                Log.e(TAG, "Device Connected Event Received")
                val deviceCaps = provisionManager!!.espDevice.deviceCapabilities
                btnConnect!!.isEnabled = true
                btnConnect!!.alpha = 1f
                txtConnectBtn!!.setText(R.string.btn_connect)
                progressBar!!.visibility = View.GONE
                arrowImage!!.visibility = View.VISIBLE
                if (!TextUtils.isEmpty(pop)) {
                    provisionManager!!.espDevice.proofOfPossession = pop
                    if (deviceCaps != null && deviceCaps.contains("wifi_scan")) {
                        goToWifiScanListActivity()
                    } else {
                        goToWiFiConfigActivity()
                    }
                } else {
                    if (deviceCaps != null && !deviceCaps.contains("no_pop") && securityType == 1) {
                        goToPopActivity()
                    } else if (deviceCaps != null && deviceCaps.contains("wifi_scan")) {
                        goToWifiScanListActivity()
                    } else {
                        goToWiFiConfigActivity()
                    }
                }
            }
            ESPConstants.EVENT_DEVICE_CONNECTION_FAILED -> {
                btnConnect!!.isEnabled = true
                btnConnect!!.alpha = 1f
                txtConnectBtn!!.setText(R.string.btn_connect)
                progressBar!!.visibility = View.GONE
                arrowImage!!.visibility = View.VISIBLE
                Toast.makeText(this, R.string.error_device_connect_failed, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    var btnConnectClickListener = View.OnClickListener {
        startActivityForResult(Intent(Settings.ACTION_WIFI_SETTINGS), WIFI_SETTINGS_ACTIVITY_REQUEST)
    }

    private fun connectDevice() {
        btnConnect!!.isEnabled = false
        btnConnect!!.alpha = 0.5f
        txtConnectBtn!!.setText(R.string.btn_connecting)
        progressBar!!.visibility = View.VISIBLE
        arrowImage!!.visibility = View.GONE
        if (ActivityCompat.checkSelfPermission(this@ProvisionLanding, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            provisionManager!!.espDevice.connectWiFiDevice()
        } else {
            Log.e(TAG, "Not able to connect device as Location permission is not granted.")
            Toast.makeText(this@ProvisionLanding, "Please give location permission to connect device", Toast.LENGTH_LONG).show()
        }
    }

    private val cancelButtonClickListener = View.OnClickListener { finish() }

    private fun initViews() {
        tvTitle = findViewById(R.id.main_toolbar_title)
        tvBack = findViewById(R.id.btn_back)
        tvCancel = findViewById(R.id.btn_cancel)
        tvTitle!!.setText(R.string.title_activity_connect_device)
        tvBack!!.setVisibility(View.GONE)
        tvCancel!!.setVisibility(View.VISIBLE)
        tvCancel!!.setOnClickListener(cancelButtonClickListener)
        btnConnect = findViewById(R.id.btn_connect)
        txtConnectBtn = findViewById(R.id.text_btn)
        arrowImage = findViewById(R.id.iv_arrow)
        progressBar = findViewById(R.id.progress_indicator)
        tvConnectDeviceInstruction = findViewById(R.id.tv_connect_device_instruction)
        tvDeviceName = findViewById(R.id.tv_device_name)
        var instruction = getString(R.string.connect_device_instruction_general)
        if (TextUtils.isEmpty(deviceName)) {
            tvConnectDeviceInstruction!!.setText(instruction)
            tvDeviceName!!.setVisibility(View.GONE)
        } else {
            instruction = getString(R.string.connect_device_instruction_specific)
            tvConnectDeviceInstruction!!.setText(instruction)
            tvDeviceName!!.setVisibility(View.VISIBLE)
            tvDeviceName!!.setText(deviceName)
        }
        txtConnectBtn!!.setText(R.string.btn_connect)
        btnConnect!!.setOnClickListener(btnConnectClickListener)
        hasPermissions()
    }

    private fun goToPopActivity() {
        finish()
        val popIntent = Intent(applicationContext, ProofOfPossessionActivity::class.java)
        startActivity(popIntent)
    }

    private fun goToWifiScanListActivity() {
        finish()
        val wifiListIntent = Intent(applicationContext, WiFiScanActivity::class.java)
        startActivity(wifiListIntent)
    }

    private fun goToWiFiConfigActivity() {
        finish()
        val wifiConfigIntent = Intent(applicationContext, WiFiConfigActivity::class.java)
        startActivity(wifiConfigIntent)
    }

    private fun hasPermissions(): Boolean {
        if (!hasLocationPermissions()) {
            requestLocationPermission()
            return false
        }
        return true
    }

    private fun hasLocationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION)
        }
    }

    companion object {
        private val TAG = ProvisionLanding::class.java.simpleName
        private const val REQUEST_FINE_LOCATION = 10
        private const val WIFI_SETTINGS_ACTIVITY_REQUEST = 11
    }
}