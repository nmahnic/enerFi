package com.nicomahnic.dadm.enerfi.ui.fragments.espprovisioning

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.espressif.provisioning.DeviceConnectionEvent
import com.espressif.provisioning.ESPConstants
import com.espressif.provisioning.ESPProvisionManager
import com.espressif.provisioning.WiFiAccessPoint
import com.espressif.provisioning.listeners.WiFiScanListener
import com.google.android.material.textfield.TextInputLayout
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.ui.adapter.WiFiListAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.util.ArrayList


class WiFiScanActivity : AppCompatActivity() {

    private val TAG = WiFiScanActivity::class.java.simpleName

    private var handler: Handler? = null
    private var ivRefresh: ImageView? = null
    private var wifiListView: ListView? = null
    private var progressBar: ProgressBar? = null
    private var adapter: WiFiListAdapter? = null
    private var wifiAPList: ArrayList<WiFiAccessPoint>? = null
    private var provisionManager: ESPProvisionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wi_fi_scan)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.title_activity_wifi_scan_list)
        setSupportActionBar(toolbar)
        ivRefresh = findViewById(R.id.btn_refresh)
        wifiListView = findViewById(R.id.wifi_ap_list)
        progressBar = findViewById(R.id.wifi_progress_indicator)
        progressBar!!.visibility = View.VISIBLE
        wifiAPList = ArrayList()
        handler = Handler()
        provisionManager = ESPProvisionManager.getInstance(applicationContext)
        val deviceName = provisionManager!!.espDevice.deviceName
        val wifiMsg = String.format(getString(R.string.setup_instructions), deviceName)
        val tvWifiMsg = findViewById<TextView>(R.id.wifi_message)
        tvWifiMsg.text = wifiMsg
        ivRefresh!!.setOnClickListener(refreshClickListener)
        adapter = WiFiListAdapter(this, R.id.tv_wifi_name, wifiAPList)

        // Assign adapter to ListView
        wifiListView!!.adapter = adapter
        wifiListView!!.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, pos, l ->
                Log.d(TAG, "Device to be connected -" + wifiAPList!![pos])
                val ssid = wifiAPList!![pos].wifiName
                when {
                    ssid == getString(R.string.join_other_network) -> {
                        askForNetwork(wifiAPList!![pos].wifiName, wifiAPList!![pos].security)
                    }
                    wifiAPList!![pos].security == ESPConstants.WIFI_OPEN.toInt() -> {
                        goForProvisioning(wifiAPList!![pos].wifiName, "")
                    }
                    else -> {
                        askForNetwork(wifiAPList!![pos].wifiName, wifiAPList!![pos].security)
                    }
                }
            }
        wifiListView!!.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom -> })
        EventBus.getDefault().register(this)
        startWifiScan()
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

    private fun startWifiScan() {
        Log.d(TAG, "Start Wi-Fi Scan")
        wifiAPList!!.clear()
        runOnUiThread {
            updateProgressAndScanBtn(true)
        }
        handler!!.postDelayed(stopScanningTask, 15000)
        provisionManager!!.espDevice.scanNetworks(object : WiFiScanListener {
            override fun onWifiListReceived(wifiList: ArrayList<WiFiAccessPoint>) {
                runOnUiThread {
                    wifiAPList!!.addAll(wifiList)
                    completeWifiList()
                }
            }

            override fun onWiFiScanFailed(e: Exception) {

                // TODO
                Log.e(TAG, "onWiFiScanFailed")
                e.printStackTrace()
                runOnUiThread {
                    updateProgressAndScanBtn(false)
                    Toast.makeText(this@WiFiScanActivity, "Failed to get Wi-Fi scan list", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun completeWifiList() {

        // Add "Join network" Option as a list item
        val wifiAp = WiFiAccessPoint()
        wifiAp.wifiName = getString(R.string.join_other_network)
        wifiAPList!!.add(wifiAp)
        updateProgressAndScanBtn(false)
        handler!!.removeCallbacks(stopScanningTask)
    }

    private fun askForNetwork(ssid: String, authMode: Int) {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_wifi_network, null)
        builder.setView(dialogView)
        val etSsid = dialogView.findViewById<EditText>(R.id.et_ssid)
        val etPassword = dialogView.findViewById<EditText>(R.id.et_password)
        if (ssid == getString(R.string.join_other_network)) {
            builder.setTitle(R.string.dialog_title_network_info)
        } else {
            builder.setTitle(ssid)
            etSsid.visibility = View.GONE
        }
        builder.setPositiveButton(R.string.btn_provision,
            DialogInterface.OnClickListener { dialog, which ->
                var password = etPassword.text.toString()
                if (ssid == getString(R.string.join_other_network)) {
                    val networkName = etSsid.text.toString()
                    if (TextUtils.isEmpty(networkName)) {
                        etSsid.error = getString(R.string.error_ssid_empty)
                    } else {
                        dialog.dismiss()
                        goForProvisioning(networkName, password)
                    }
                } else {
                    if (TextUtils.isEmpty(password)) {
                        if (authMode != ESPConstants.WIFI_OPEN.toInt()) {
                            val passwordLayout: TextInputLayout =
                                dialogView.findViewById(R.id.layout_password)
                            passwordLayout.error = getString(R.string.error_password_empty)
                        } else {
                            dialog.dismiss()
                            goForProvisioning(ssid, password)
                        }
                    } else {
                        if (authMode == ESPConstants.WIFI_OPEN.toInt()) {
                            password = ""
                        }
                        dialog.dismiss()
                        goForProvisioning(ssid, password)
                    }
                }
            })
        builder.setNegativeButton(R.string.btn_cancel,
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun goForProvisioning(ssid: String, password: String) {
        finish()
//        val provisionIntent = Intent(applicationContext, ProvisionActivity::class.java)
//        provisionIntent.putExtras(intent)
//        provisionIntent.putExtra(AppConstants.KEY_WIFI_SSID, ssid)
//        provisionIntent.putExtra(AppConstants.KEY_WIFI_PASSWORD, password)
//        startActivity(provisionIntent)
    }

    private val refreshClickListener = View.OnClickListener {
        startWifiScan()
    }

    private val stopScanningTask = Runnable {
        updateProgressAndScanBtn(false)
    }

    /**
     * This method will update UI (Scan button enable / disable and progressbar visibility)
     */
    private fun updateProgressAndScanBtn(isScanning: Boolean) {
        if (isScanning) {
            progressBar!!.visibility = View.VISIBLE
            wifiListView!!.visibility = View.GONE
            ivRefresh!!.visibility = View.GONE
        } else {
            progressBar!!.visibility = View.GONE
            wifiListView!!.visibility = View.VISIBLE
            ivRefresh!!.visibility = View.VISIBLE
            adapter!!.notifyDataSetChanged()
        }
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