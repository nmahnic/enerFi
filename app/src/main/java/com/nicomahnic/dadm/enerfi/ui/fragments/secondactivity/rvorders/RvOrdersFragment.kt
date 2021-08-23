package com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.rvorders
import androidx.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.core.Resource
import com.nicomahnic.dadm.enerfi.data.DataSource
import com.nicomahnic.dadm.enerfi.data.database.AppDatabase
import com.nicomahnic.dadm.enerfi.repository.RepositoryImpl
import com.nicomahnic.dadm.enerfi.ui.activities.SecondActivity
import com.nicomahnic.dadm.enerfi.ui.adapter.OrdersAdapter
import com.nicomahnic.dadm.enerfi.viewmodel.RvOrdersViewModel
import com.nicomahnic.dadm.enerfi.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.rv_orders_fragment.*

class RvOrdersFragment : Fragment(R.layout.rv_orders_fragment) {

//    private lateinit var binding: RvOrdersFragmentBinding
    private lateinit var v: View
    private val viewModel by viewModels<RvOrdersViewModel>{
        ViewModelFactory(
            RepositoryImpl(
                DataSource(
                    requireContext(),
                    AppDatabase.getAppDataBase(requireActivity().applicationContext)
                )
            )
        )
    }

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onViewCreated (view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding = RvOrdersFragmentBinding.bind(view)

        v = view

        Log.d("NM", "Singleton ${SecondActivity.User.name}")

    }

    override fun onStart() {
        super.onStart()

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        Log.d("NM",prefs.getString("language","es")!!)

        rv_orders.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        rv_orders.layoutManager = linearLayoutManager

        viewModel.fetchOrderList().observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Resource.Loading -> {
                    Log.d("LiveData", "Loading...")
                }
                is Resource.Failure -> {
                    Log.d("LiveData", "${result.exception}")
                }
                is Resource.Success -> {
                    result.data?.let{ orderList ->
                        ordersAdapter = OrdersAdapter(orderList) { pos ->
                            Log.d("NM", pos.toString())
                            val action =
                                RvOrdersFragmentDirections.actionRvOrdersFragmentToTabContainerFragment(
                                    orderNum = orderList[pos].id,
                                    clientName = orderList[pos].name
                                )

                            v.findNavController().navigate(action)
                        }
                        rv_orders.adapter = ordersAdapter
                    }
                }
            }
        })

    }

}