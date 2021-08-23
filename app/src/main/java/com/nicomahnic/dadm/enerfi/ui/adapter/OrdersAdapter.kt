package com.nicomahnic.dadm.enerfi.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicomahnic.dadm.enerfi.R
import com.nicomahnic.dadm.enerfi.data.entities.OrderEntity
import kotlinx.android.synthetic.main.item_order.view.*

class OrdersAdapter(
    private var orderList: List<OrderEntity>,
    val onItemClick: (Int) -> Unit
): RecyclerView.Adapter<OrdersAdapter.OrderHolder>() {

//    private lateinit var binding: ItemOrderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_order,parent,false)
        return (OrderHolder(view))
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        holder.setItem(orderList[position])

        holder.getItem(position)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    inner class OrderHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setItem(order: OrderEntity) {
//            binding = ItemOrderBinding.bind(itemView)
            itemView.txt_name_item.text = order.name
            itemView.txt_order_num.text = order.id.toString().padStart(5,'0')
        }

        fun getItem (position: Int): Unit {
            return itemView.setOnClickListener { onItemClick(position) }
        }
    }
}