package com.nicomahnic.dadm.enerfi.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.core.base.BaseViewHolder
import com.nicomahnic.dadm.enerfi.data.entities.Book
import com.nicomahnic.dadm.enerfi.databinding.ItemBookBinding
import com.nicomahnic.dadm.enerfi.databinding.ItemOrderbookBinding
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.second_activity.*

class OrderBooksAdapter(
    private val context: Context,
    private val bookList: List<Book>,
    private val itemClickListener: OnBookClickListener
): RecyclerView.Adapter<BaseViewHolder<*>>(){

    interface OnBookClickListener {
        fun onBookClick(position: Int, book: Book)
    }

    private lateinit var binding: ItemOrderbookBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_orderbook,parent,false)
        return (OrderHolder(view))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int)  {
        when(holder){
            is OrderHolder -> holder.bind(bookList[position], position)
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    inner class OrderHolder (itemView: View) : BaseViewHolder<Book>(itemView) {

        override fun bind(item: Book, position: Int) {
            binding = ItemOrderbookBinding.bind(itemView)
            binding.txtBookTitle.text = item.title
            binding.txtAuthor.text = item.author
            binding.txtYear.text = item.year.toString()
            Glide.with(context) //1
                .load(item.imgUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .skipMemoryCache(true) //2
                .diskCacheStrategy(DiskCacheStrategy.NONE) //3
                .transform(CircleCrop()) //4
                .into(binding.imgItem)
            itemView.setOnClickListener { itemClickListener.onBookClick(position, item) }
        }
    }
}