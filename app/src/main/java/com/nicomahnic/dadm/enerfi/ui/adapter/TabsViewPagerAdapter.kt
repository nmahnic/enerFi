package com.nicomahnic.dadm.enerfi.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.tabcontainer.bookdetails.BookDetailsFragment
import com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.tabcontainer.rvbooks.RvBooksFragment

class TabsViewPagerAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {

        return when(position){
            0 -> BookDetailsFragment()
            1 -> RvBooksFragment()
            else -> RvBooksFragment()
        }
    }

    override fun getItemCount(): Int {
        return TAB_COUNT
    }

    companion object {
        private const val TAB_COUNT = 2
    }
}