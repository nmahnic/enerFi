package com.nicomahnic.dadm.enerfi.ui.fragments.secondactivity.tabcontainer.rvbooks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.core.Resource
import com.nicomahnic.dadm.enerfi.databinding.RvBooksFragmentBinding
import com.nicomahnic.dadm.enerfi.data.entities.Book
import com.nicomahnic.dadm.enerfi.ui.adapter.BooksAdapter
import com.nicomahnic.dadm.enerfi.viewmodel.TabContainerViewModel

class RvBooksFragment : Fragment(R.layout.rv_books_fragment) {

    private lateinit var binding: RvBooksFragmentBinding
    private val viewModelTab: TabContainerViewModel by activityViewModels()
    private lateinit var v: View

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var booksAdapter: BooksAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RvBooksFragmentBinding.bind(view)
        v = view
    }

    override fun onStart() {
        super.onStart()

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        Log.d("NM",prefs.getString("language","es")!!)

        binding.rvBooks.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        binding.rvBooks.layoutManager = linearLayoutManager

        viewModelTab.currentOrder.observe(viewLifecycleOwner, { result ->
            Log.d("NM", "fetchOrder $result")
            booksAdapter = BooksAdapter(requireContext(), getBooks(result.books))
            binding.rvBooks.adapter = booksAdapter
        })
    }

    private fun getBooks(bookAsJson: String): List<Book> {
        return Gson().fromJson(bookAsJson, Array<Book>::class.java).toList()
    }

}