package com.nicomahnic.tests.sender

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.drawer.RegisterOrderFragment

class ESPtransaction private constructor(val context: Context){

    companion object{
        @Volatile
        private var INSTANCE: ESPtransaction? = null
        const val REQUEST_CODE = 255


        fun getInstance(context: Context): ESPtransaction {
            val tempInstance = INSTANCE

            tempInstance?.let { return tempInstance }

            synchronized(this) {
                val instance = ESPtransaction(context)
                INSTANCE = instance
                return instance
            }
        }
    }

    fun launchIngpPinpad(data: RegisterOrderFragment.EspRequest, pm: PackageManager) :
            Pair<Intent, Int>{
        Log.d("NM", "1) Envio ${data}")


        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra("inputData",data.inputData)
        sendIntent.type = "text/plain"


        val sharedIntent = CustomSenderIntent.create(pm,sendIntent,"com.espressif.wifi_provisioning")
        return Pair(sharedIntent, REQUEST_CODE)
    }

}