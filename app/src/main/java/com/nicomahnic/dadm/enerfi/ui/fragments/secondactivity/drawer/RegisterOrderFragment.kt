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
import com.nicomahnic.tests.sender.Payment
import kotlinx.android.synthetic.main.register_order_fragment.*

class RegisterOrderFragment : Fragment(R.layout.register_order_fragment) { //R.layout.register_order_fragment

    private lateinit var payment: Payment
    private var v: View? = null

    private var transactionResult = ""
    private var errorCode = ""
    private var issuer = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        goToWiFiProvisionLandingActivity()

        v = super.onCreateView(inflater, container, savedInstanceState)
        return v
    }

    private fun goToWiFiProvisionLandingActivity() {
        val transaction = DoPayment(
            currency = "UYU",
            currencyCode = 858,
            transactionType = TransactionType.SALE.name,
            amount = 12.50,
        )
        payment = Payment.getInstance(requireContext())
        val newIntent = payment.launchIngpPinpad(transaction, requireActivity().packageManager)

        startActivityForResult(newIntent.first,newIntent.second)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Payment.REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            transactionResult = data!!.getStringExtra("transactionResult")!!
            errorCode = data.getStringExtra("errorCode") ?: ""
            issuer = data.getStringExtra("issuer") ?: ""
            Log.d("NM", "2) Respuesta transactionResult:${transactionResult}")
            Log.d("NM", "2) Respuesta errorCode:${errorCode}")
            Log.d("NM", "2) Respuesta issuer:${issuer}")

            tvNormal1.text = transactionResult
            tvNormal2.text = errorCode

        }
    }

}

data class DoPayment(
    val currency: String,
    val currencyCode: Int,
    val transactionType: String,
    val amount: Double
)

data class PaymentResault(
    val transactionResault: String,
    val errorCode: String,
    val issuer: String,
    val installments: Int,
    val approvedCode: String,
    val rrn: String,
    val maskedCardNo: String
)

enum class TransactionType {
    SALE,
    OFFLINE_SALE,
    VOID,
    REFUND,
}