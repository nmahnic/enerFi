package com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.drawer

import android.app.Activity
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.budiyev.android.codescanner.CodeScanner
import com.espressif.provisioning.ESPConstants
import com.espressif.provisioning.ESPProvisionManager
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.data.DataSource
import com.nicomahnic.dadm.enerfi.data.database.AppDatabase
import com.nicomahnic.dadm.enerfi.data.entities.Book
import com.nicomahnic.dadm.enerfi.repository.RepositoryImpl
import com.nicomahnic.dadm.enerfi.ui.fragments.espprovisioning.ProvisionLanding
import com.nicomahnic.dadm.enerfi.viewmodel.RegisterOrderViewModel
import com.nicomahnic.dadm.enerfi.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.register_order_fragment.*
import java.lang.Exception

class RegisterOrderFragment : Fragment() { //R.layout.register_order_fragment

//    private lateinit var binding: RegisterOrderFragmentBinding
    private lateinit var v: View

    private var networkEnabled: Boolean = false

    private val viewModel: RegisterOrderViewModel by activityViewModels() {
        ViewModelFactory(
            RepositoryImpl(
                DataSource(
                    requireContext(),
                    AppDatabase.getAppDataBase(requireActivity().applicationContext)
                )
            )
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        try {
            val lm = requireContext().getSystemService(Activity.LOCATION_SERVICE) as LocationManager
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        val securityType = 1
        val provisionManager = ESPProvisionManager.getInstance(requireContext())
        provisionManager.createESPDevice(ESPConstants.TransportType.TRANSPORT_SOFTAP, ESPConstants.SecurityType.SECURITY_1)
        goToWiFiProvisionLandingActivity(securityType)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun goToWiFiProvisionLandingActivity(securityType: Int) {
        val wifiProvisioningIntent = Intent(requireContext(), ProvisionLanding::class.java)
        wifiProvisioningIntent.putExtra(KEY_SECURITY_TYPE, securityType)
        startActivity(wifiProvisioningIntent)
    }

    companion object {
        const val KEY_SECURITY_TYPE = "security_type"
    }

}