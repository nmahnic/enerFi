package com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.drawer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.tests.sender.ESPtransaction
import kotlinx.android.synthetic.main.register_order_fragment.*

class RegisterOrderFragment : Fragment(R.layout.register_order_fragment) { //R.layout.register_order_fragment

    private lateinit var espTransaction: ESPtransaction
    private var v: View? = null

    private var deviceName = ""
    private var ssid = ""
    private var errorCode = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        
        goToWiFiProvisionLandingActivity()

        v = super.onCreateView(inflater, container, savedInstanceState)
        return v
    }

    private fun goToWiFiProvisionLandingActivity() {
        val transaction = EspRequest(
            inputData = "UYU"
        )
        espTransaction = ESPtransaction.getInstance(requireContext())
        val newIntent = espTransaction.launchIngpPinpad(transaction, requireActivity().packageManager)

        startActivityForResult(newIntent.first,newIntent.second)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ESPtransaction.REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            deviceName = data!!.getStringExtra("deviceName")!!
            errorCode = data.getStringExtra("errorCode") ?: ""
            ssid = data.getStringExtra("ssid") ?: ""
            Log.d("NM", "2) Respuesta -> deviceName: ${deviceName}")
            Log.d("NM", "2) Respuesta -> errorCode:  ${errorCode}")
            Log.d("NM", "2) Respuesta -> ssid:       ${ssid}")

            edtDeviceName.setText(deviceName)
            edtSsid.setText(ssid)

        }
    }

    data class EspRequest(
        val inputData: String
    )

}



