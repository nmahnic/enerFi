package com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.tabcontainer.bookdetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.core.Resource
import com.nicomahnic.dadm.enerfi.viewmodel.TabContainerViewModel
import kotlinx.android.synthetic.main.book_details_fragment.*

class BookDetailsFragment : Fragment(R.layout.book_details_fragment) {

    private val viewModelTab: TabContainerViewModel by activityViewModels()
//    private lateinit var binding: BookDetailsFragmentBinding
    private lateinit var v: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding = BookDetailsFragmentBinding.bind(view)
        v = view
    }

    override fun onStart() {
        super.onStart()

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        Log.d("NM",prefs.getString("language","es")!!)

        viewModelTab.fetchOrder().observe(viewLifecycleOwner, { result ->
            Log.d("NM", "fetchOrder $result")
            when(result){
                is Resource.Loading -> {
                    Log.d("LiveData", "Loading...")
                }
                is Resource.Success -> {
                    txt_order_num.text = result.data!!.id.toString().padStart(5,'0')
                    edt_title.setText(result.data!!.name)
                }
                is Resource.Failure -> {
                    Log.d("LiveData", "${result.exception}")
                }
            }
        })

        btn_modify.setOnClickListener {
            val newName = edt_title.text.toString()
            viewModelTab.updateOrder(newName)
            Toast.makeText(requireContext(), "Nombre Modificado", Toast.LENGTH_SHORT).show()
        }

        btn_delete.setOnClickListener {
            viewModelTab.deleteOrder()
            Toast.makeText(requireContext(), "Orden Eliminada", Toast.LENGTH_SHORT).show()
        }
    }

}