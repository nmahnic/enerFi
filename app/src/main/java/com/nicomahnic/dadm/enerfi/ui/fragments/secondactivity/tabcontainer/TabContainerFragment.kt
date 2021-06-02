package com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.tabcontainer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.material.tabs.TabLayoutMediator
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.core.Resource
import com.nicomahnic.dadm.enerfi.data.DataSource
import com.nicomahnic.dadm.enerfi.data.database.AppDatabase
import com.nicomahnic.dadm.enerfi.databinding.TabContainerFragmentBinding
import com.nicomahnic.dadm.enerfi.repository.RepositoryImpl
import com.nicomahnic.dadm.enerfi.ui.adapter.BooksAdapter
import com.nicomahnic.dadm.enerfi.ui.adapter.TabsViewPagerAdapter
import com.nicomahnic.dadm.enerfi.viewmodel.TabContainerViewModel
import com.nicomahnic.dadm.enerfi.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.tab_container_fragment.*

class TabContainerFragment : Fragment(R.layout.tab_container_fragment) {

    private lateinit var binding: TabContainerFragmentBinding
    private lateinit var v: View
    private val viewModelTab: TabContainerViewModel by activityViewModels() {
        ViewModelFactory(
            RepositoryImpl(
                DataSource(
                    requireContext(),
                    AppDatabase.getAppDataBase(requireActivity().applicationContext)
                )
            )
        )
    }
    private val args: TabContainerFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TabContainerFragmentBinding.bind(view)

        v = view
        viewPager.adapter = TabsViewPagerAdapter(requireActivity())

        Log.d("NM", "in TabContainer")
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        Log.d("NM",prefs.getString("language","es")!!)

        viewModelTab.loadOrder(args.orderNum)
        viewModelTab.fetchOrder().observe(viewLifecycleOwner, { result ->
            Log.d("NM", "fetchOrder $result")
            when(result){
                is Resource.Loading -> {
                    Log.d("LiveData", "Loading...")
                }
                is Resource.Success -> {
                    viewModelTab.loadCurrentOrder(result.data!!)
                }
                is Resource.Failure -> {
                    Log.d("LiveData", "${result.exception}")
                }
            }
        })

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.tab_order)
                1 -> tab.text = getString(R.string.tab_books)
                else -> tab.text = "undefined"
            }
        }.attach()
    }
}