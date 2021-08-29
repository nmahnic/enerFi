package com.nicomahnic.tests.sender

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.drawer.DoPayment
import com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.drawer.PaymentResault

class Payment private constructor(val context: Context){

    companion object{
        @Volatile
        private var INSTANCE: Payment? = null
        const val REQUEST_CODE = 255


        fun getInstance(context: Context): Payment {
            val tempInstance = INSTANCE

            tempInstance?.let { return tempInstance }

            synchronized(this) {
                val instance = Payment(context)
                INSTANCE = instance
                return instance
            }
        }
    }

    fun launchIngpPinpad(data: DoPayment, pm: PackageManager) :
            Pair<Intent, Int>{
        Log.d("NM", "1) Envio ${data}")


        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra("currency",data.currency)
        sendIntent.putExtra("currencyCode",data.currencyCode)
        sendIntent.putExtra("transactionType",data.transactionType)
        sendIntent.putExtra("amount",data.amount)
        sendIntent.type = "text/plain"


        val sharedIntent = CustomSenderIntent.create(pm,sendIntent,"com.espressif.wifi_provisioning")
        return Pair(sharedIntent, REQUEST_CODE)
    }

}