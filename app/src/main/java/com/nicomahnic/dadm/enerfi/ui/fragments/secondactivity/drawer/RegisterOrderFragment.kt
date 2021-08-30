package com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.drawer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.data.DataSource
import com.nicomahnic.dadm.enerfi.data.database.AppDatabase
import com.nicomahnic.dadm.enerfi.databinding.RegisterOrderFragmentBinding
import com.nicomahnic.dadm.enerfi.repository.RepositoryImpl
import com.nicomahnic.dadm.enerfi.viewmodel.RegisterDeviceViewModel
import com.nicomahnic.dadm.enerfi.viewmodel.ViewModelFactory
import com.nicomahnic.tests.sender.ESPtransaction
import kotlinx.android.synthetic.main.register_order_fragment.*

class RegisterOrderFragment : Fragment(R.layout.register_order_fragment) { //R.layout.register_order_fragment

    private lateinit var espTransaction: ESPtransaction
    private lateinit var binding: RegisterOrderFragmentBinding
    private val viewModel by viewModels<RegisterDeviceViewModel>{
        ViewModelFactory(
            RepositoryImpl(
                DataSource(
                    requireContext(),
                    AppDatabase.getAppDataBase(requireActivity().applicationContext)
                )
            )
        )
    }

    private var deviceName = ""
    private var ssid = ""
    private var errorCode = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RegisterOrderFragmentBinding.bind(view)

        binding.btnConfigESP.setOnClickListener{
            goToWiFiProvisionLandingActivity()
        }

        binding.btnSaveDevice.setOnClickListener {
            viewModel.insertOrder(
                clientName = deviceName,
                bookList = emptyList()
            )
            view.findNavController().popBackStack()
        }
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



