package com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.drawer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.espressif.provisioning.ESPProvisionManager
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.core.Resource
import com.nicomahnic.dadm.enerfi.data.DataSource
import com.nicomahnic.dadm.enerfi.data.database.AppDatabase
import com.nicomahnic.dadm.enerfi.data.entities.Book
import com.nicomahnic.dadm.enerfi.databinding.RegisterOrderFragmentBinding
import com.nicomahnic.dadm.enerfi.repository.RepositoryImpl
import com.nicomahnic.dadm.enerfi.viewmodel.RegisterOrderViewModel
import com.nicomahnic.dadm.enerfi.viewmodel.ViewModelFactory

class RegisterOrderFragment : Fragment(R.layout.register_order_fragment) {

    private lateinit var binding: RegisterOrderFragmentBinding
    private lateinit var v: View

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

    private var bookList = mutableListOf<Book>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RegisterOrderFragmentBinding.bind(view)

        v = view
    }

    override fun onStart() {
        super.onStart()

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        Log.d("NM",prefs.getString("language","es")!!)

        val hola = ESPProvisionManager.getInstance(context)

        binding.btnEnter.setOnClickListener {
            val clientName = "NM"
            viewModel.insertOrder(clientName, bookList)
            Toast.makeText(requireContext(), "Orden Cargada", Toast.LENGTH_SHORT).show()
        }
    }
}